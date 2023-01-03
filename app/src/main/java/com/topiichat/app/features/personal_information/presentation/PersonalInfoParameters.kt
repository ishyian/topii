package com.topiichat.app.features.personal_information.presentation

import android.os.Parcelable
import com.topiichat.app.features.registration.domain.model.ProfileDomain
import kotlinx.parcelize.Parcelize

@Parcelize
data class PersonalInfoParameters(
    val profileDomain: ProfileDomain
) : Parcelable