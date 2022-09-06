package com.topiichat.app.features.send_remittance.presentation.model

import com.topiichat.app.features.home.domain.model.AvailableCountriesDomain
import com.topiichat.app.features.home.domain.model.CountryDomain
import com.topiichat.app.features.send_remittance.domain.model.FxRateDomain

data class SendRemittanceUIModel(
    var fxRate: FxRateDomain?,
    val availableCountries: AvailableCountriesDomain,
    val currentCountry: CountryDomain,
)