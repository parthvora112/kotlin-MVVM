package com.teco.apparchitecture.util

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.net.toUri
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*


object AppUtil {

    fun convertDpToPixel(dp: Float, context: Context): Int {
        val resources: Resources = context.resources
        val metrics: DisplayMetrics = resources.displayMetrics
        return (dp * (metrics.densityDpi / 160f)).toInt()
    }

    enum class OptionType {
        TYPE_FORM_VALIDATION,
        TYPE_API_CALL,
        TYPE_DATA_BASE,
        TYPE_CLOUD_DATA_BASE,
        TYPE_DIFF_UTILS
    }

    fun showSnackMessage(
        context: Context?,
        view: View?,
        message: String?
    ) {
        if (context == null || view == null || message == null) return
        Snackbar.make(
            context,
            view,
            message,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    fun toggleSoftKeyBoard(
        context: Context,
        view: View,
        isHide: Boolean
    ) {
        val imm =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (isHide) {
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        } else {
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    fun encodeImage(activity: Activity, cropImageFile: File?): String? {
        if (cropImageFile == null) return null
        val input = activity.contentResolver.openInputStream(cropImageFile.toUri())
        val image = BitmapFactory.decodeStream(input, null, null)

        val baos = ByteArrayOutputStream()
        image?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageBytes = baos.toByteArray()
        return Base64.getEncoder().encodeToString(imageBytes)
    }

    fun decodeImage(imageString: String): Bitmap? {
        val imageBytes = Base64.getDecoder().decode(imageString)
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.e("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.e("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.e("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }
}