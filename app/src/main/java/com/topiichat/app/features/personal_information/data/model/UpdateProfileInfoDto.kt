package com.topiichat.app.features.personal_information.data.model

import com.squareup.moshi.Json
import com.topiichat.core.data.Dto

data class UpdateProfileInfoDto(
    @Json(name = "firstName")
    val firstName: String?,
    @Json(name = "lastName")
    val lastName: String?,
    @Json(name = "firstNameSecond")
    val firstNameSecond: String?,
    @Json(name = "lastNameSecond")
    val lastNameSecond: String?
) : Dto