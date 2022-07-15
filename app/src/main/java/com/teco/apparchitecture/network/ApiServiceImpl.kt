package com.teco.apparchitecture.network

import com.teco.apparchitecture.model.Post
import javax.inject.Inject

class ApiServiceImpl @Inject constructor(private val apiService: ApiService) {

    suspend fun getPost():List<Post> = apiService.getPost()
}