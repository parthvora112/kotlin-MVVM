package com.teco.apparchitecture.ui.firebase_chat.otp_verification

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class OtpVerificationViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    fun requestForOtp(activity: Activity, userPhoneNumber: String?) {
        if (userPhoneNumber == null) return
        _otpVerificationUiState.value = OTPVerificationUiState.Loading
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(userPhoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun validateOtp(userOtp: String) {
        if (verificationId == null) {
            _otpVerificationUiState.value =
                OTPVerificationUiState.Error("Something went wrong, please try again later.")
            return
        }
        _otpVerificationUiState.value = OTPVerificationUiState.Loading
        val credential = PhoneAuthProvider.getCredential(verificationId!!, userOtp)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    _otpVerificationUiState.value = OTPVerificationUiState.Success
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        _otpVerificationUiState.value = OTPVerificationUiState.Error("Invalid OTP.")
                    }
                }
            }

    }

    private var verificationId: String? = null
    private var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
//                Log.d(TAG, "onVerificationCompleted:$credential")
//                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e)

                _otpVerificationUiState.value = OTPVerificationUiState.Error(e.localizedMessage)
                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }

                // Show a message and update the UI
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:$verificationId")
                _otpVerificationUiState.value = OTPVerificationUiState.Empty
                this@OtpVerificationViewModel.verificationId = verificationId

                // Save verification ID and resending token so we can use them later
//                storedVerificationId = verificationId
//                resendToken = token
            }
        }
    private val _otpVerificationUiState =
        MutableStateFlow<OTPVerificationUiState>(OTPVerificationUiState.Empty)
    val otpVerificationUiState = _otpVerificationUiState.asStateFlow()

    sealed class OTPVerificationUiState {
        object Empty : OTPVerificationUiState()
        object Success : OTPVerificationUiState()
        data class Error(val errorMessage: String?) : OTPVerificationUiState()
        object Loading : OTPVerificationUiState()
    }

    companion object {
        const val TAG = "OtpVerificationViewModel"
    }
}