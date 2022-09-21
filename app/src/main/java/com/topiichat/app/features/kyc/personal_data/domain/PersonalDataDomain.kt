package com.topiichat.app.features.kyc.personal_data.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PersonalDataDomain(
    val firstName: String,
    val lastName: String,
    val isoCode2: String,
    val secondLastName: String?
) : Parcelable