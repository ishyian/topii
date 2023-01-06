package com.topiichat.remittance.features.new_beneficiary.data.model

import com.squareup.moshi.Json
import com.topiichat.core.data.Dto

data class BeneficiaryDocumentTypeDto(
    @Json(name = "id")
    val id: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "value")
    val value: String
) : Dto