package com.topiichat.remittance.features.new_beneficiary.data.mapper

import com.topiichat.core.domain.Mapper
import com.topiichat.remittance.features.new_beneficiary.data.model.BeneficiaryCountryDto
import com.topiichat.remittance.features.new_beneficiary.domain.model.CountryDomain
import javax.inject.Inject

class BeneficiaryCountriesListMapper @Inject constructor() : Mapper<List<BeneficiaryCountryDto>, List<CountryDomain>> {
    override fun map(input: List<BeneficiaryCountryDto>?): List<CountryDomain> {
        return input?.let { unboxedList ->
            unboxedList.map { countryDto ->
                CountryDomain(
                    id = countryDto.id.toInt(),
                    name = countryDto.name,
                    countryCodes = if (countryDto.dialCountryCode != null) countryDto.dialCountryCode.map {
                        it.code
                    }
                    else if (countryDto.countryCode != null) listOf(
                        countryDto.countryCode
                    ) else emptyList()
                )
            }
        } ?: emptyList()
    }
}