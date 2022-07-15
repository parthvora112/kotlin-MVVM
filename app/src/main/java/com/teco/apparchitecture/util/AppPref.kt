package com.teco.apparchitecture.util

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri

object AppPref {
    private const val APP_PREF_NAME = "app.architecture.pref"
    private const val APP_PREF_MODE = Context.MODE_PRIVATE

    private const val PREF_KEY_CAPTURE_IMAGE_URI = "pref.key.capture.image"

    fun storeCaptureImagePath(context: Context, uri: String) {
        val pref = getPref(context)
        pref.edit().putString(
            PREF_KEY_CAPTURE_IMAGE_URI,
            uri
        ).apply()
    }

    fun getCaptureImageUri(
        context: Context
    ) : Uri? {
        return Uri.parse(
            getPref(context).getString(
                PREF_KEY_CAPTURE_IMAGE_URI,
                null
            )
        )
    }

    private fun getPref(context: Context): SharedPreferences {
        return context.getSharedPreferences(
            APP_PREF_NAME,
            APP_PREF_MODE
        )
    }
}