package com.teco.apparchitecture.ui.firebase_chat.chat_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseApp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.toObject
import com.teco.apparchitecture.model.ChatListModel
import com.teco.apparchitecture.model.FirebaseAppUser
import com.teco.apparchitecture.model.Message
import com.teco.apparchitecture.util.AppConstants.KEY_FIRE_STORE_CHAT_MEMBER_PATH
import com.teco.apparchitecture.util.AppConstants.KEY_FIRE_STORE_LAST_CHAT_PATH
import com.teco.apparchitecture.util.AppConstants.KEY_FIRE_STORE_MESSAGES_PATH
import com.teco.apparchitecture.util.AppConstants.KEY_FIRE_STORE_USER_PATH
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import kotlin.collections.ArrayList
import kotlin.math.log

@HiltViewModel
class ChatListViewModel @Inject constructor(
    @Named(KEY_FIRE_STORE_MESSAGES_PATH)
    private val messageCollection: CollectionReference,
    @Named(KEY_FIRE_STORE_USER_PATH)
    private val usersCollection: CollectionReference,
) : ViewModel() {

    private val _chatListUiState = MutableStateFlow<ChatListUiState>(ChatListUiState.Empty)
    val chatListUiState = _chatListUiState.asStateFlow()

    private var firebaseUser: FirebaseUser? = null

    fun fetchUserChatsList() {
        viewModelScope.launch {
            firebaseUser = FirebaseAuth.getInstance(FirebaseApp.getInstance()).currentUser
            if (firebaseUser == null) return@launch
            messageCollection
                .whereArrayContains(
                    KEY_FIRE_STORE_CHAT_MEMBER_PATH,
                    firebaseUser!!.uid
                )
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Log.e(TAG, "fetchUserChatsList::Error $error")
                        return@addSnapshotListener
                    }
                    if (value?.documents!!.isEmpty()) {
                        Log.e(TAG, "fetchUserChatsList::No data ===== No chat founded...")
                        return@addSnapshotListener
                    }
                    for (document in value.documents) {
                        var chatUserId: String
                        val chatMembers =
                            document.data?.get(KEY_FIRE_STORE_CHAT_MEMBER_PATH) as ArrayList<String>
                        chatUserId = if (chatMembers[0] == firebaseUser?.uid.toString()) {
                            chatMembers[1]
                        } else {
                            chatMembers[0]
                        }
                        val lastMessage =
                            document.data!![KEY_FIRE_STORE_LAST_CHAT_PATH] as HashMap<String, String>?
                        var lastMessageToShow: Message? = null
                        var isAddUserToChatList = true
                        if (lastMessage != null) {
                            var lastMessageTimestamp =
                                lastMessage!!["timeStamp"] as Timestamp?
                            lastMessageTimestamp =
                                if (lastMessageTimestamp != null) {
                                    Timestamp(Date(lastMessageTimestamp.toDate().time))
                                } else {
                                    Timestamp.now()
                                }
//                        if (deletedMessageTimestamp != null) {
//                            if (deletedMessageTimestamp.seconds > lastMessageTimestamp?.seconds!!) {
//                                isAddUserToChatList = false
//                            }
//                        }
                            if(lastMessage["message"]!!.toString().isNullOrBlank()){
                                isAddUserToChatList = false
                            }

                            lastMessageToShow = Message(
                                lastMessage["message"]!!.toString(),
                                lastMessage["fromId"]!!.toString(),
                                lastMessage["toId"]!!.toString(),
                                lastMessageTimestamp
                            )
//                        Log.e(TAG, "fetchUserChatsList:: lastMessageToShow ----> $lastMessageToShow")
                        }
                        if(!isAddUserToChatList) return@addSnapshotListener
                        usersCollection
                            .document(chatUserId)
                            .get().addOnSuccessListener { documentSnapshot ->
                                val chatUser = documentSnapshot.toObject<FirebaseAppUser>()
                                if (chatUser != null) {
                                    chatUser.lastMessage = lastMessageToShow
                                    _chatListUiState.value = ChatListUiState.NewChatFound(chatUser)
                                }
                            }
                    }
                }
        }
    }

    fun logoutUser() {
        FirebaseAuth.getInstance(FirebaseApp.getInstance()).signOut()
    }

    val tempChatList = mutableListOf<ChatListModel>()

    companion object {
        const val TAG = "ChatListViewModel"
    }


    sealed class ChatListUiState {
        object Empty : ChatListUiState()
        data class NewChatFound(val firebaseAppUser: FirebaseAppUser) : ChatListUiState()
    }


}