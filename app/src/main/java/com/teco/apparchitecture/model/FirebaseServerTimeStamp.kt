package com.teco.apparchitecture.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class FirebaseServerTimeStamp(
    @ServerTimestamp
    val timeStamp: Timestamp? = null
)