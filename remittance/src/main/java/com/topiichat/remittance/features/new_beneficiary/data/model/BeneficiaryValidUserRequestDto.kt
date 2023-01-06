package com.topiichat.remittance.features.new_beneficiary.data.model

import com.squareup.moshi.Json
import com.topiichat.core.data.Dto

data class BeneficiaryValidUserRequestDto(
    @Json(name = "dialCountryCode")
    val dialCountryCode: String,
    @Json(name = "mobileNumber")
    val mobileNumber: String
) : Dto