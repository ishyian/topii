package com.topiichat.app.features.home.domain.model

import com.topiichat.core.domain.Domain

data class AvailableCountriesDomain(
    val countries: List<CountryDomain>
) : Domain