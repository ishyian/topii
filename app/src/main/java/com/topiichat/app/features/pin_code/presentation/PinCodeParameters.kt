package com.topiichat.app.features.pin_code.presentation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PinCodeParameters(
    val phoneNumber: String,
    val authyId: String,
    val code: String,
    val isoCode: String
) : Parcelable