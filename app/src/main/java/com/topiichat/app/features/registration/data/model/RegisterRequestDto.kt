package com.topiichat.app.features.registration.data.model

import com.squareup.moshi.Json
import com.topiichat.app.core.data.Dto
import com.topiichat.app.features.valid_phone_number.data.model.PhoneNumberDto
import java.util.UUID

data class RegisterRequestDto(
    @Json(name = "authyId")
    val authyId: String,
    @Json(name = "pin")
    val pinCode: String,
    @Json(name = "phoneNumber")
    val phoneNumber: PhoneNumberDto,
    @Json(name = "userIdAlice")
    val userIdAlice: String = UUID.randomUUID().toString()
) : Dto