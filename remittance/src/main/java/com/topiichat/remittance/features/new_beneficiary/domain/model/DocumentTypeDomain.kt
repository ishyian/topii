package com.topiichat.remittance.features.new_beneficiary.domain.model

import com.topiichat.core.domain.Domain

data class DocumentTypeDomain(
    val id: Int,
    val name: String,
    val value: String
) : Domain
