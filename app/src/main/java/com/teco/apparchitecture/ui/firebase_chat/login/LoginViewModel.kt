package com.teco.apparchitecture.ui.firebase_chat.login

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    val firebaseUser: FirebaseUser?
) : ViewModel() {

    private val _loginUiState = MutableStateFlow<LoginUiState>(LoginUiState.Empty)
    val loginUiState = _loginUiState.asStateFlow()

    sealed class LoginUiState{
        object Empty : LoginUiState()
        object Loading : LoginUiState()
        object SuccessGoogleLogin: LoginUiState()
    }
}