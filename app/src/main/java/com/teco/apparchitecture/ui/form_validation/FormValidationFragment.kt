package com.teco.apparchitecture.ui.form_validation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.teco.apparchitecture.databinding.FragmentFormValidationBinding
import com.teco.apparchitecture.ui.BaseFragment
import com.teco.apparchitecture.util.AppUtil
import kotlinx.coroutines.flow.collect


class FormValidationFragment : BaseFragment<FragmentFormValidationBinding>() {

    private val viewModel: FormValidationViewModel by viewModels()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentFormValidationBinding
        get() = fun(
            layoutInflater: LayoutInflater,
            viewGroup: ViewGroup?,
            isAttachToParent: Boolean
        ) = FragmentFormValidationBinding.inflate(layoutInflater, viewGroup, isAttachToParent)

    override fun viewCreated(view: View) {
        requireActivity().title = "Form validation"
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        lifecycleScope.launchWhenStarted {
            viewModel.loginUiState.collect {
                when(it){
                    FormValidationViewModel.FormUiState.Empty -> {
                        binding.etEmail.setText("")
                        binding.etPassword.setText("")
                        binding.progressCircular.isVisible = false
                    }
                    is FormValidationViewModel.FormUiState.Error -> {
                        AppUtil.showSnackMessage(
                            context,
                            binding.root,
                            it.message
                        )
                        binding.progressCircular.isVisible = false
                    }
                    FormValidationViewModel.FormUiState.Loading -> {
                        binding.progressCircular.isVisible = true
                    }
                    FormValidationViewModel.FormUiState.Success -> {
                        AppUtil.showSnackMessage(
                            context,
                            binding.root,
                            "Success"
                        )
                        binding.progressCircular.isVisible = false
                    }
                }
            }
        }

        binding.btnSubmit.setOnClickListener {
            viewModel.login(
                binding.etEmail.text.toString(),
                binding.etPassword.text.toString(),
            )
        }
    }


    override fun onStartApplication() {}

    override fun onPauseApplication() {}

}