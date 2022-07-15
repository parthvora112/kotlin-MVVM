package com.teco.apparchitecture.ui.form_validation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FormValidationViewModel @Inject constructor() : ViewModel() {

    private val _loginUiState = MutableStateFlow<FormUiState>(FormUiState.Empty)
    val loginUiState = _loginUiState.asStateFlow()

    fun login(
        username: String,
        password: String
    ) = viewModelScope.launch {
        _loginUiState.value = FormUiState.Loading
        delay(2000L)
        if (username == "test" && password == "test")
            _loginUiState.value = FormUiState.Success
        else _loginUiState.value = FormUiState.Error(message = "InValid Credential")
    }

    sealed class FormUiState {
        object Success : FormUiState()
        data class Error(val message: String) : FormUiState()
        object Loading : FormUiState()
        object Empty : FormUiState()
    }
}