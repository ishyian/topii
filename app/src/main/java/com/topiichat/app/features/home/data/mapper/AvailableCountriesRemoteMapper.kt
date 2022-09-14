package com.topiichat.app.features.home.data.mapper

import com.topiichat.app.core.domain.Mapper
import com.topiichat.app.features.home.data.model.AvailableCountryDto
import com.topiichat.app.features.home.domain.model.AvailableCountriesDomain
import com.topiichat.app.features.home.domain.model.CountryDomain
import javax.inject.Inject

class AvailableCountriesRemoteMapper @Inject constructor() :
    Mapper<List<AvailableCountryDto>, AvailableCountriesDomain> {
    override fun map(input: List<AvailableCountryDto>?): AvailableCountriesDomain {
        return AvailableCountriesDomain(
            countries = input?.map { country ->
                CountryDomain(
                    allowedFrom = country.allowedFrom,
                    allowedTo = country.allowedTo,
                    code = country.code,
                    currencyCode = country.currencyCode,
                    flagImageUrl = country.flag,
                    kycFields = emptyList(), //TODO Change to actual KYC models
                    limitMax = country.limitMax.toDouble(),
                    limitMin = country.limitMin.toDouble(),
                    name = country.name,
                    preferred = country.preferred,
                    dialCountryCode = country.dialCodeNumber
                )
            } ?: emptyList()
        )
    }
}