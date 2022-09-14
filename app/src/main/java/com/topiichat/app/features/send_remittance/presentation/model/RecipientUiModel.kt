package com.topiichat.app.features.send_remittance.presentation.model

import com.topiichat.app.features.home.domain.model.CountryDomain
import com.topiichat.app.features.home.domain.model.RecentUserDomain

data class RecipientUiModel(
    val userData: RecentUserDomain,
    val recipientCountry: CountryDomain?
)
