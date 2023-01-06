package com.topiichat.remittance.features.new_beneficiary.domain.model

import com.topiichat.core.domain.Domain

data class CountryDomain(
    val countryCodes: List<String>,
    val id: Int,
    val name: String
) : Domain