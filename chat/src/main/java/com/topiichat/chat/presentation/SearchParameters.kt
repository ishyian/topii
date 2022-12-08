package com.topiichat.chat.presentation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchParameters(
    val uuid: String? = null
) : Parcelable