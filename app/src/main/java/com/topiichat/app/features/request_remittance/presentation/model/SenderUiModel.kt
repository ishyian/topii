package com.topiichat.app.features.request_remittance.presentation.model

import com.topiichat.app.features.home.domain.model.CountryDomain
import com.topiichat.app.features.home.domain.model.RecentUserDomain

data class SenderUiModel(
    val userData: RecentUserDomain,
    val senderCountry: CountryDomain?
)
