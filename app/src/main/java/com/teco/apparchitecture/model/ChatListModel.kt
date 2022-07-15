package com.teco.apparchitecture.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChatListModel(
    val chatId: String? = null,
    val userName: String? = null
) : Parcelable
