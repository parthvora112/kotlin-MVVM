package com.teco.apparchitecture.model

import com.teco.apparchitecture.util.AppUtil

data class OptionModel(
    val type : AppUtil.OptionType? = null,
    val title: String? = null,
    val resId: Int? = null
)
