package com.teco.apparchitecture.ui.firebase_chat.otp_verification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.teco.apparchitecture.R
import com.teco.apparchitecture.databinding.FragmentOtpVerificationBinding
import com.teco.apparchitecture.ui.BaseFragment
import com.teco.apparchitecture.util.AppConstants
import com.teco.apparchitecture.util.AppUtil
import com.teco.apparchitecture.util.AppUtil.toggleSoftKeyBoard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class OtpVerificationFragment : BaseFragment<FragmentOtpVerificationBinding>() {

    private val viewModel: OtpVerificationViewModel by viewModels()

    private var userPhoneNumber: String? = null

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentOtpVerificationBinding
        get() = fun(
            layoutInflater: LayoutInflater,
            viewGroup: ViewGroup?,
            isAttachToParent: Boolean
        ) = FragmentOtpVerificationBinding.inflate(
            layoutInflater,
            viewGroup,
            isAttachToParent
        )

    override fun viewCreated(view: View) {
        initBundleData()
        initViews()
        initObserver()
    }

    private fun initObserver() {
        lifecycleScope.launchWhenStarted {
            viewModel.otpVerificationUiState.collect {
                when (it) {
                    OtpVerificationViewModel.OTPVerificationUiState.Empty -> {
                        binding.progressCircular.isVisible = false
                    }
                    is OtpVerificationViewModel.OTPVerificationUiState.Error -> {
                        binding.progressCircular.isVisible = false
                        if (it.errorMessage == null) {
                            showSnackMessage("Something went wrong, Please try after some time")
                        } else {
                            showSnackMessage(it.errorMessage)
                        }
                    }
                    OtpVerificationViewModel.OTPVerificationUiState.Loading -> {
                        binding.progressCircular.isVisible = true
                    }
                    OtpVerificationViewModel.OTPVerificationUiState.Success -> {
                        binding.progressCircular.isVisible = false
                        navigateToFragment(R.id.action_otpVerificationFragment_to_editProfileFragment)
                    }
                }
            }
        }
    }

    private fun initBundleData() {
        if (arguments == null) return
        userPhoneNumber = requireArguments().getString(AppConstants.KEY_USER_PHONE_NO)
        viewModel.requestForOtp(
            requireActivity(),
            userPhoneNumber
        )
    }

    private fun initViews() {
        userPhoneNumber?.let {
            binding.tvPhoneInfo.text = "Your OTP will receive at $userPhoneNumber."
        }
        binding.btnSubmit.setOnClickListener {
            toggleSoftKeyBoard(
                requireContext(),
                binding.root,
                true
            )
            val userOtp = binding.pinView.value
            if (isNotLoading() && isValidInput(userOtp)) {
                viewModel.validateOtp(userOtp)
            }
        }
    }

    private fun isValidInput(userOtp: String?): Boolean {
        if (userOtp.isNullOrEmpty()) {
            showSnackMessage("Please enter OTP")
            return false
        }
        if (userOtp.length < 6) {
            showSnackMessage("Please enter 6 digit code.")
            return false
        }
        return true
    }

    private fun isNotLoading(): Boolean {
        if (viewModel.otpVerificationUiState.value == OtpVerificationViewModel.OTPVerificationUiState.Loading) {
            showSnackMessage(requireContext().getString(R.string.loading_message))
            return false
        }
        return true
    }

    override fun onStartApplication() {}

    override fun onPauseApplication() {}

}