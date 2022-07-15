package com.teco.apparchitecture.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Message(
    val message: String? = null,
    val fromId: String? = null,
    val toId: String? = null,
    @ServerTimestamp
    var timeStamp: Timestamp? = null
): Parcelable{
    @get:Exclude
    var isShowDate = false
}