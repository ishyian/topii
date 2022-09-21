package com.topiichat.app.features.kyc.email.domain

import com.topiichat.app.core.domain.Domain

data class OnboardingDomain(
    val tokenAlice: String,
    val iso3CountryCode: String?
) : Domain
