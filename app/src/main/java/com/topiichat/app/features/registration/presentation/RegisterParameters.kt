package com.topiichat.app.features.registration.presentation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RegisterParameters(
    val phoneNumber: String = "",
    val authyId: String = " ",
    val code: String = "",
    val pinCode: String = "",
    val isoCode: String = "",
    val aliceUserId: String = "",
    val isFromAuth: Boolean = true
) : Parcelable