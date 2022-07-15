package com.teco.apparchitecture.ui.diff_util

import androidx.lifecycle.ViewModel
import com.teco.apparchitecture.model.DiffUtilDataModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DiffUtilDemoViewModel @Inject constructor() : ViewModel() {

    var itemList = mutableListOf(
        DiffUtilDataModel(1, "Status 1", false),
        DiffUtilDataModel(2, "Status 2", false),
        DiffUtilDataModel(3, "Status 3", false),
        DiffUtilDataModel(4, "Status 4", false),
        DiffUtilDataModel(5, "Status 5", false),
        DiffUtilDataModel(6, "Status 6", false),
        DiffUtilDataModel(7, "Status 7", false),
        DiffUtilDataModel(8, "Status 8", false),
        DiffUtilDataModel(9, "Status 9", false),
        DiffUtilDataModel(10, "Status 10", false),
        DiffUtilDataModel(11, "Status 11", false),
        DiffUtilDataModel(12, "Status 12", false),
        DiffUtilDataModel(13, "Status 13", false),
        DiffUtilDataModel(14, "Status 14", false),
        DiffUtilDataModel(15, "Status 15", false),
        DiffUtilDataModel(16, "Status 16", false),
        DiffUtilDataModel(17, "Status 17", false),
        DiffUtilDataModel(18, "Status 18", false),
        DiffUtilDataModel(19, "Status 19", false),
        DiffUtilDataModel(20, "Status 20", false)
    )
}