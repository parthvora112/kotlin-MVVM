package com.teco.apparchitecture.repository

import com.teco.apparchitecture.model.Post
import com.teco.apparchitecture.network.ApiServiceImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class NetworkRepository @Inject constructor(
    private val apiServiceImpl: ApiServiceImpl
) {
    fun getPost(): Flow<List<Post>> = flow {
        emit(apiServiceImpl.getPost())
    }.flowOn(Dispatchers.IO)
}