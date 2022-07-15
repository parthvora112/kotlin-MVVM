package com.teco.apparchitecture.ui.firebase_chat.login

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.teco.apparchitecture.R
import com.teco.apparchitecture.databinding.FragmentLoginBinding
import com.teco.apparchitecture.ui.BaseFragment
import com.teco.apparchitecture.util.AppConstants.KEY_USER_PHONE_NO
import com.teco.apparchitecture.util.AppUtil.showSnackMessage
import com.teco.apparchitecture.util.AppUtil.toggleSoftKeyBoard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.collect
import javax.inject.Inject


@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {

    private val viewModel: LoginViewModel by viewModels()

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    @Inject
    lateinit var auth: FirebaseAuth

    @ObsoleteCoroutinesApi
    private var googleSigningIntentLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val googleCredential = oneTapClient.getSignInCredentialFromIntent(result.data)
                val idToken = googleCredential.googleIdToken
                when {
                    idToken != null -> {
                        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                        auth.signInWithCredential(firebaseCredential)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.e(TAG, "signInWithCredential:success")
                                } else {
                                    Log.e(TAG, "signInWithCredential:failure", task.exception)
                                }
                            }
                    }
                    else -> {
                        Log.d(TAG, "No ID token!")
                    }
                }

            } else if (result.resultCode == Activity.RESULT_CANCELED) {
                Log.e(TAG, "registerForActivityResult::  Activity.RESULT_CANCELED")
            }
        }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLoginBinding
        get() = fun(
            layoutInflater: LayoutInflater,
            viewGroup: ViewGroup?,
            isAttachToParent: Boolean
        ) = FragmentLoginBinding.inflate(
            layoutInflater,
            viewGroup,
            isAttachToParent
        )

    @ObsoleteCoroutinesApi
    override fun viewCreated(view: View) {
        initViews()
        initObservers()
    }

    private fun initObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.loginUiState.collect {
                when(it){
                    LoginViewModel.LoginUiState.Empty -> {
                        binding.progressCircular.isVisible = false
                        binding.etPhoneNo.setText("")
                    }
                    LoginViewModel.LoginUiState.Loading -> {
                        binding.progressCircular.isVisible = true
                    }
                    LoginViewModel.LoginUiState.SuccessGoogleLogin -> {
                        binding.progressCircular.isVisible = false
                    }
                }
            }
        }
    }

    private fun initViews() {
        Log.e(TAG, "initViews: FirebaseUser :: ${viewModel.firebaseUser} ")
        binding.btnSubmit.setOnClickListener {
            val phoneNo = binding.etCountryCode.text.toString() + binding.etPhoneNo.text.toString()
            if(isNotLoading() && isValidInput(phoneNo)){
                toggleSoftKeyBoard(
                    requireContext(),
                    binding.etPhoneNo,
                    true
                )
                navigateToFragment(R.id.action_loginFragment_to_otpVerificationFragment, Bundle().apply { putString(KEY_USER_PHONE_NO,phoneNo) })
            }
        }
    }

    private fun isValidInput(phoneNo: String): Boolean {
        if(!android.util.Patterns.PHONE.matcher(phoneNo).matches()){
            showSnackMessage(
                requireContext(),
                binding.root,
                "Please enter valid phone no."
            )
            return false
        }
        return true
    }

    private fun isNotLoading(): Boolean {
        if(viewModel.loginUiState.value == LoginViewModel.LoginUiState.Loading){
            showSnackMessage(
                requireContext(),
                binding.root,
                "Please wait until loading.."
            )
            return false
        }
        return true
    }


    @OptIn(ObsoleteCoroutinesApi::class)
    private fun googleLogin(){
        if (viewModel.firebaseUser == null) {
            oneTapClient = Identity.getSignInClient(requireActivity())
            signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(
                    BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build()
                )
                .setGoogleIdTokenRequestOptions(
                    BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(getString(R.string.web_client_id))
                        .setFilterByAuthorizedAccounts(true)
                        .build()
                )
                .setAutoSelectEnabled(true)
                .build()
            oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener { result ->
                    googleSigningIntentLauncher.launch(
                        IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                    )
                }
                .addOnFailureListener {
                }

        } else {
            //already login
        }
    }

    override fun onStartApplication() {}

    override fun onPauseApplication() {}
}