package com.topiichat.core.presentation.error.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class Error : Parcelable {
    NO_NETWORK,
    INSUFFICIENT_BALANCE,
    WRONG_REQUEST,
    UNKNOWN
}