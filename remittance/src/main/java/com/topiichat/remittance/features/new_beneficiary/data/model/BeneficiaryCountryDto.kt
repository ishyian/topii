package com.topiichat.remittance.features.new_beneficiary.data.model

import com.squareup.moshi.Json

data class BeneficiaryCountryDto(
    @Json(name = "country_code")
    val countryCode: String?,
    @Json(name = "dial_country_code")
    val dialCountryCode: List<DialCountryCode>?,
    @Json(name = "id")
    val id: String,
    @Json(name = "name")
    val name: String
) {
    data class DialCountryCode(
        @Json(name = "code")
        val code: String
    )
}