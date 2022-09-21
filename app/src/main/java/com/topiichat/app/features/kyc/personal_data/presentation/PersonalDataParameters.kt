package com.topiichat.app.features.kyc.personal_data.presentation

import android.os.Parcelable
import com.topiichat.app.features.kyc.base.domain.model.KYCRegisterDomain
import kotlinx.parcelize.Parcelize

@Parcelize
data class PersonalDataParameters(
    val isoCode2: String,
    val registerModel: KYCRegisterDomain? = null
) : Parcelable
