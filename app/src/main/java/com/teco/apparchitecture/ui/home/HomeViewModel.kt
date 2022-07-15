package com.teco.apparchitecture.ui.home

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.teco.apparchitecture.R
import com.teco.apparchitecture.model.OptionModel
import com.teco.apparchitecture.util.AppUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val firebaseUser: FirebaseUser?
): ViewModel() {
    val optionsList = listOf(
        OptionModel(AppUtil.OptionType.TYPE_FORM_VALIDATION,"Form Validation", R.drawable.img_form),
        OptionModel(AppUtil.OptionType.TYPE_API_CALL,"Retrofit Api Call", R.drawable.img_networking),
        OptionModel(AppUtil.OptionType.TYPE_DATA_BASE,"Room Database", R.drawable.img_database),
        OptionModel(AppUtil.OptionType.TYPE_CLOUD_DATA_BASE,"Firebase (chat)", R.drawable.img_cloud_database),
        OptionModel(AppUtil.OptionType.TYPE_DIFF_UTILS,"DiffUtils", R.drawable.img_diff_ways),
    )
}