package com.teco.apparchitecture.ui.firebase_chat.single_chat

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.teco.apparchitecture.model.FirebaseAppUser
import com.teco.apparchitecture.model.FirebaseServerTimeStamp
import com.teco.apparchitecture.model.Message
import com.teco.apparchitecture.util.AppConstants.KEY_FIRE_STORE_CHAT_MEMBER_PATH
import com.teco.apparchitecture.util.AppConstants.KEY_FIRE_STORE_LAST_CHAT_PATH
import com.teco.apparchitecture.util.AppConstants.KEY_FIRE_STORE_MESSAGES_PATH
import com.teco.apparchitecture.util.AppConstants.KEY_FIRE_STORE_USERS_MESSAGES_PATH
import com.teco.apparchitecture.util.AppConstants.KEY_FIRE_STORE_USER_PATH
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Named


@HiltViewModel
class SingleChatViewModel @Inject constructor(
    @Named(KEY_FIRE_STORE_USER_PATH)
    private val usersCollection: CollectionReference,
    @Named(KEY_FIRE_STORE_MESSAGES_PATH)
    private val messageCollection: CollectionReference,
) : ViewModel() {

    private val _singleChatUiState = MutableStateFlow<SingleChatUiState>(SingleChatUiState.Empty)
    val singleChatUiState = _singleChatUiState.asStateFlow()

    fun sendMessage(message: String) {
        if (message.isEmpty()) return
        Message(
            message,
            currentUserId,
            firebaseChatUser?.uid
        ).let { messageModel ->
            messageCollection.document(chatDocumentId!!)
                .collection(KEY_FIRE_STORE_USERS_MESSAGES_PATH)
                .document()
                .set(
                    messageModel
                )
            messageCollection.document(chatDocumentId!!)
                .update(
                    KEY_FIRE_STORE_LAST_CHAT_PATH,
                    messageModel
                )
        }

        messageCollection.document(chatDocumentId!!)
            .get()
            .addOnSuccessListener { documentSnapShot ->
                if (!documentSnapShot.data?.contains(KEY_FIRE_STORE_CHAT_MEMBER_PATH)!!) {
                    addChatMembers()
                } else {
                    val chatMembers =
                        documentSnapShot.data!![KEY_FIRE_STORE_CHAT_MEMBER_PATH] as ArrayList<String>
                    if (chatMembers.isEmpty()) {
                        addChatMembers()
                    }
                }
            }

    }

    private fun addChatMembers() {
        messageCollection.document(chatDocumentId!!)
            .update(
                KEY_FIRE_STORE_CHAT_MEMBER_PATH,
                FieldValue.arrayUnion(
                    currentUserId,
                    firebaseChatUser?.uid!!
                )
            )
    }

    fun initFirebaseChat() {
            chatDocumentId = if (currentUserId!! < firebaseChatUser?.uid!!)
                "${currentUserId}_${firebaseChatUser?.uid}"
            else
                "${firebaseChatUser?.uid}_${currentUserId}"

            messageCollection.whereEqualTo(
                FieldPath.documentId(),
                chatDocumentId
            ).get()
                .addOnSuccessListener { querySnapShot ->
                    if (querySnapShot.documents.isNotEmpty()) {
                        chatDocumentId = querySnapShot.documents[0].id
//                    initChatObserver(chatListener)
                    } else {
                        messageCollection
                            .document(chatDocumentId!!)
                            .set(HashMap<String, Any>())
                        messageCollection.document(chatDocumentId!!).update(
                            "delete_${currentUserId}",
                            FirebaseServerTimeStamp()
                        ).addOnCompleteListener {
                            messageCollection.document(chatDocumentId!!).update(
                                "delete_${firebaseChatUser?.uid!!}",
                                FirebaseServerTimeStamp()
                            ).addOnSuccessListener {
//                            initChatObserver(chatListener)
                            }
                        }
                    }
                    Log.e("chatDocumentId", chatDocumentId!!)
                }

            val fireStoreQuery = messageCollection
                .document(chatDocumentId!!).collection("chatMessages")
        _singleChatUiState.value = SingleChatUiState.Success(fireStoreQuery)
    }

    var firebaseChatUser: FirebaseAppUser? = null
    var currentUserId: String? = null

    private var chatDocumentId: String? = null

    init {
        currentUserId = FirebaseAuth.getInstance(FirebaseApp.getInstance()).currentUser?.uid
    }

    sealed class SingleChatUiState {
        data class Success(val fireStoreQuery: CollectionReference) : SingleChatUiState()
        object Empty : SingleChatUiState()
    }
}