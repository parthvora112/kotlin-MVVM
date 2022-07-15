package com.teco.apparchitecture.ui.retrofit_api_call

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teco.apparchitecture.model.Post
import com.teco.apparchitecture.repository.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RetrofitApiCallViewModel @Inject constructor(
    private val repository: NetworkRepository
) : ViewModel() {

    private val _getPostStateFlow: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Loading)
    val getPostStateFlow = _getPostStateFlow.asStateFlow()


    fun getPost() = viewModelScope.launch {
        _getPostStateFlow.value = ApiState.Loading
        repository.getPost()
            .catch { e ->
                _getPostStateFlow.value = ApiState.Failure(e)
            }
            .collect { postList ->
                _getPostStateFlow.value = ApiState.Success(postList)
            }
    }

    sealed class ApiState {
        object Loading : ApiState()
        class Failure(val msg: Throwable) : ApiState()
        class Success(val data: List<Post>) : ApiState()
        object Empty : ApiState()
    }
}