package com.topiichat.app.features.kyc.email.presentation

import android.os.Parcelable
import com.topiichat.app.features.kyc.base.domain.model.KYCRegisterDomain
import com.topiichat.app.features.kyc.personal_data.domain.PersonalDataDomain
import kotlinx.parcelize.Parcelize

@Parcelize
data class EnterEmailParameters(
    val personalData: PersonalDataDomain,
    val registerModel: KYCRegisterDomain? = null
) : Parcelable