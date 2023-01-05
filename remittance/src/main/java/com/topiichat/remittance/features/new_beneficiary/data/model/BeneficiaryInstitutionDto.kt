package com.topiichat.remittance.features.new_beneficiary.data.model

import com.squareup.moshi.Json

data class BeneficiaryInstitutionDto(
    @Json(name = "country_id")
    val countryId: String,
    @Json(name = "fields")
    val fields: List<String>,
    @Json(name = "id")
    val id: String,
    @Json(name = "name")
    val name: String
)