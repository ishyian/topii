package com.topiichat.app.features.kyc.base.domain.model

import android.os.Parcelable
import com.topiichat.app.core.domain.Domain
import com.topiichat.app.features.registration.presentation.RegisterParameters
import kotlinx.parcelize.Parcelize

@Parcelize
data class KYCRegisterDomain(
    val phoneNumber: String,
    val authyId: String,
    val code: String,
    val pinCode: String,
    val isoCode2: String
) : Domain, Parcelable

fun KYCRegisterDomain.toRegisterParameters(
    aliceUserId: String
) = RegisterParameters(
    phoneNumber = phoneNumber,
    authyId = authyId,
    code = code,
    pinCode = pinCode,
    isoCode = isoCode2,
    aliceUserId = aliceUserId,
    isFromAuth = false
)

