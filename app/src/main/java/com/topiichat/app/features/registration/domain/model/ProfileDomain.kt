package com.topiichat.app.features.registration.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProfileDomain(
    val firstName: String,
    val lastName: String,
    val firstNameSecond: String,
    val lastNameSecond: String,
    val avatar: String
) : Parcelable