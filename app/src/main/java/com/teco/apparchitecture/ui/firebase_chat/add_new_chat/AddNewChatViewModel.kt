package com.teco.apparchitecture.ui.firebase_chat.add_new_chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.toObject
import com.teco.apparchitecture.model.FirebaseAppUser
import com.teco.apparchitecture.util.AppConstants.KEY_FIRE_STORE_USER_PATH
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class AddNewChatViewModel @Inject constructor(
    @Named(KEY_FIRE_STORE_USER_PATH)
    private val usersCollection: CollectionReference
) : ViewModel() {

    private val _addNewChatUiState = MutableStateFlow<AddNewChatUiState>(AddNewChatUiState.Empty)
    val addNewChatUiState = _addNewChatUiState.asStateFlow()

    private var firebaseUser: FirebaseUser? = null

    fun fetchAllUsers() {
        viewModelScope.launch {
            _addNewChatUiState.value = AddNewChatUiState.Loading
            firebaseUser = FirebaseAuth.getInstance(FirebaseApp.getInstance()).currentUser
            usersCollection.get().addOnSuccessListener { querySnapShot ->
                val allUsers = arrayListOf<FirebaseAppUser>()
                for (document in querySnapShot) {
                    if (document.id != firebaseUser?.uid)
                        allUsers.add(document.toObject())
                }
                Log.e("TAG", "fetchAllUsers: $allUsers" )
                _addNewChatUiState.value = AddNewChatUiState.Success(allUsers)
            }.addOnFailureListener {
                Log.e("usersCollection::addOnFailureListener::", it.localizedMessage)
                _addNewChatUiState.value = AddNewChatUiState.Error(it.localizedMessage)
            }
        }
    }


    sealed class AddNewChatUiState{
        object Empty: AddNewChatUiState()
        object Loading: AddNewChatUiState()
        data class Success(val users: List<FirebaseAppUser>): AddNewChatUiState()
        data class Error(val errorMessage: String?): AddNewChatUiState()

    }
}