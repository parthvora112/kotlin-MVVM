package com.teco.apparchitecture.ui.firebase_chat.edit_profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.teco.apparchitecture.R
import com.teco.apparchitecture.databinding.FragmentEditProfileBinding
import com.teco.apparchitecture.ui.BaseFragment
import com.teco.apparchitecture.util.AppUtil
import com.teco.apparchitecture.util.AppUtil.isInternetAvailable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class EditProfileFragment : BaseFragment<FragmentEditProfileBinding>() {

    private val viewModel: EditProfileViewModel by viewModels()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentEditProfileBinding
        get() = fun(
            layoutInflater: LayoutInflater,
            viewGroup: ViewGroup?,
            isAttachToParent: Boolean
        ) = FragmentEditProfileBinding.inflate(
            layoutInflater,
            viewGroup,
            isAttachToParent
        )

    override fun viewCreated(view: View) {
        initViews()
        initObservers()
    }

    private fun initObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.editProfileUiState.collect {
                when (it) {
                    EditProfileViewModel.EditProfileUiState.Empty -> {
                        initLoadingFinishView()
                    }
                    is EditProfileViewModel.EditProfileUiState.Error -> {
                        initLoadingFinishView()
                        if (it.errorMessage == null) {
                            showSnackMessage(getString(R.string.something_wrong))
                        } else {
                            showSnackMessage(it.errorMessage)
                        }
                    }
                    EditProfileViewModel.EditProfileUiState.Loading -> {
                        initLoadingView()
                    }
                    EditProfileViewModel.EditProfileUiState.EditUpdateProfileSuccess -> {
                        initLoadingFinishView()
                        navigateToFragment(R.id.action_editProfileFragment_to_chatListFragment)
                    }
                    EditProfileViewModel.EditProfileUiState.FetchProfileSuccess -> {
                        initLoadingFinishView()
                        viewModel.firebaseAppUser?.profile?.let { image->
                            Glide.with(binding.root).load(AppUtil.decodeImage(image))
                                .placeholder(R.drawable.img_user_placeholder).circleCrop()
                                .into(binding.ivProfile)
                        }
                        binding.etUserName.setText(viewModel.firebaseAppUser?.name)
                    }
                }
            }
        }
    }

    private fun initLoadingFinishView() {
        binding.progressCircular.isVisible = false
        binding.etUserName.isEnabled = true
        binding.ivProfile.isEnabled = true
    }

    private fun initLoadingView() {
        binding.progressCircular.isVisible = true
        binding.etUserName.isEnabled = false
        binding.ivProfile.isEnabled = false
    }

    private fun initViews() {
        binding.btnSubmit.setOnClickListener {
            AppUtil.toggleSoftKeyBoard(
                requireContext(),
                binding.root,
                true
            )
            if (!isInternetAvailable(requireContext())) {
                showSnackMessage(requireContext().getString(R.string.no_internet))
                return@setOnClickListener
            }
            if (!isLoading() && isValidInput()) {
                viewModel.addOrUpdateUserToFirebase(
                    requireActivity(),
                    binding.etUserName.text.toString(),
                    cropImageFile
                )
            }
        }
        binding.ivProfile.setOnClickListener {
            showImageSelectionDialog()
        }
    }

    private fun isValidInput(): Boolean {
        if (binding.etUserName.text.toString().trim().isEmpty()) {
            showSnackMessage("Please add Username.")
            return false
        }
        return true
    }


    private fun isLoading(): Boolean {
        return if (viewModel.editProfileUiState.value == EditProfileViewModel.EditProfileUiState.Loading) {
            showSnackMessage(requireContext().getString(R.string.loading_message))
            true
        } else false
    }


    override fun onStartApplication() {}

    override fun onPauseApplication() {}

    override fun imageSelectedSuccess() {
        super.imageSelectedSuccess()
        Glide.with(this).load(cropImageFile!!).circleCrop().into(binding.ivProfile)
    }

}