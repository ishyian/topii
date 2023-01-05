package com.topiichat.remittance.features.new_beneficiary.data.model

import com.squareup.moshi.Json

data class BeneficiaryProductType(
    @Json(name = "id")
    val id: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "value")
    val value: String
)