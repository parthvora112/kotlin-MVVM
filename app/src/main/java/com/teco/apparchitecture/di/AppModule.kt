package com.teco.apparchitecture.di

import android.content.Context
import androidx.room.Room
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.teco.apparchitecture.R
import com.teco.apparchitecture.network.ApiService
import com.teco.apparchitecture.room.ToDoDatabase
import com.teco.apparchitecture.util.AppConstants.KEY_FIRE_STORE_MESSAGES_PATH
import com.teco.apparchitecture.util.AppConstants.KEY_FIRE_STORE_USER_PATH
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesUrl() = "https://jsonplaceholder.typicode.com/"

    @Provides
    @Singleton
    fun providesApiService(url: String): ApiService =
        Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideRoomDataBase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, ToDoDatabase::class.java, "ToDoDatabase").build()

    @Provides
    @Singleton
    fun provideToDoDao(toDoDatabase: ToDoDatabase) = toDoDatabase.getToDoDao()

    @Provides
    @Singleton
    fun provideCurrentAuth() : FirebaseUser? = FirebaseAuth.getInstance(FirebaseApp.getInstance()).currentUser

    @Provides
    @Singleton
    fun provideFireAuth() : FirebaseAuth = FirebaseAuth.getInstance(FirebaseApp.getInstance())

    @Provides
    @Singleton
    fun provideGoogleSignInOptions(@ApplicationContext context: Context): GoogleSignInOptions =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.web_client_id))
            .requestEmail()
            .build()

    @Provides
    fun provideFirestore() = Firebase.firestore

    @Provides
    @Named(KEY_FIRE_STORE_USER_PATH)
    fun provideUsersCollection(fireStore: FirebaseFirestore) =
        fireStore.collection(KEY_FIRE_STORE_USER_PATH)


    @Provides
    @Named(KEY_FIRE_STORE_MESSAGES_PATH)
    fun provideMessageCollections(fireStore: FirebaseFirestore) =
        fireStore.collection(KEY_FIRE_STORE_MESSAGES_PATH)

}