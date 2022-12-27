package com.topiichat.app.features.send_remittance.data.model

import com.squareup.moshi.Json
import com.topiichat.core.data.Dto

data class RemittancePurposeDto(
    @Json(name = "label")
    val label: String,
    @Json(name = "value")
    val value: String
) : Dto