package com.topiichat.app.features.otp.presentation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OtpParameters(
    val phoneNumber: String,
    val authyId: String,
    val code: String,
    val isoCode: String
) : Parcelable