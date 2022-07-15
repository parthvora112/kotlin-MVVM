package com.teco.apparchitecture.ui.firebase_chat.edit_profile

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.toObject
import com.teco.apparchitecture.model.FirebaseAppUser
import com.teco.apparchitecture.util.AppConstants.KEY_FIRE_STORE_USER_PATH
import com.teco.apparchitecture.util.AppUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    @Named(KEY_FIRE_STORE_USER_PATH)
    private val usersCollection: CollectionReference
) : ViewModel() {

    private var firebaseUser: FirebaseUser? = null
    var firebaseAppUser: FirebaseAppUser? = null

    private val _editProfileUiState = MutableStateFlow<EditProfileUiState>(EditProfileUiState.Empty)
    val editProfileUiState = _editProfileUiState.asStateFlow()

    init {
        viewModelScope.launch {
            firebaseUser = FirebaseAuth.getInstance(FirebaseApp.getInstance()).currentUser
            firebaseUser?.let {
                _editProfileUiState.value = EditProfileUiState.Loading
                usersCollection.document(
                    firebaseUser?.uid!!
                ).get().addOnSuccessListener { documentSnapshot ->
                    firebaseAppUser = documentSnapshot.toObject<FirebaseAppUser>()
                    _editProfileUiState.value = EditProfileUiState.FetchProfileSuccess
                }
            }

        }
    }

    fun addOrUpdateUserToFirebase(activity: Activity, name: String, imageFile: File? = null) {
        _editProfileUiState.value = EditProfileUiState.Loading
        val encodedImage =
            if (imageFile == null && firebaseAppUser?.profile == null) null else if (firebaseAppUser?.profile != null) {
                firebaseAppUser?.profile
            } else {
                AppUtil.encodeImage(activity, imageFile)
            }
        if(name == firebaseAppUser?.name && encodedImage == firebaseAppUser?.profile){
            _editProfileUiState.value = EditProfileUiState.EditUpdateProfileSuccess
            return
        }
        usersCollection.document(
            firebaseUser?.uid!!
        ).set(
            FirebaseAppUser(
                uid = firebaseUser?.uid!!,
                name = name,
                profile = encodedImage
            )
        ).addOnSuccessListener {
            _editProfileUiState.value = EditProfileUiState.EditUpdateProfileSuccess
        }.addOnFailureListener {
            _editProfileUiState.value = EditProfileUiState.Error(it.localizedMessage)
        }
    }

    sealed class EditProfileUiState {
        object Empty : EditProfileUiState()
        object Loading : EditProfileUiState()
        object EditUpdateProfileSuccess : EditProfileUiState()
        object FetchProfileSuccess : EditProfileUiState()
        data class Error(val errorMessage: String?) : EditProfileUiState()
    }

}