package com.teco.apparchitecture.network

import com.teco.apparchitecture.model.Post
import retrofit2.http.GET

interface ApiService {

    @GET("posts")
   suspend fun getPost():List<Post>
}