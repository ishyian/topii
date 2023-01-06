package com.topiichat.remittance.features.new_beneficiary.domain.model

import com.topiichat.core.domain.Domain

data class InstitutionDomain(
    val countryId: Int,
    val fields: List<String>,
    val id: String,
    val name: String
) : Domain