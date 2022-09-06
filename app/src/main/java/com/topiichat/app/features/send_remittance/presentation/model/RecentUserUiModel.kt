package com.topiichat.app.features.send_remittance.presentation.model

import com.topiichat.app.features.home.domain.model.RecentUserDomain

data class RecentUserUiModel(
    val data: RecentUserDomain,
    var isSelected: Boolean = false
)