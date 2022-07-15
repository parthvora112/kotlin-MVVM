package com.teco.apparchitecture.model

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseAppUser(
    val uid: String? = null,
    val name: String? = null,
    val profile: String? = null,
): Parcelable{

    @get:Exclude
    var lastMessage: Message? = null
}

