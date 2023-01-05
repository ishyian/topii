package com.topiichat.app.features.splash.data.model

import com.squareup.moshi.Json
import com.topiichat.core.data.Dto

data class ValidateAppDto(
    @Json(name = "accessToken")
    val accessToken: String?,
    @Json(name = "id")
    val id: String?,
    @Json(name = "isActive")
    val isActive: Boolean,
    @Json(name = "nickName")
    val nickName: String?,
    @Json(name = "streamToken")
    val streamToken: String?
) : Dto
