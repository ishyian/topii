package com.topiichat.app.features.contacts.domain.model

import com.topiichat.app.core.domain.Domain
import com.topiichat.app.features.contacts.presentation.model.ContactUiModel

data class ContactDomain(
    val displayName: String,
    val lastName: String,
    val telephone: String
) : Domain

fun ContactDomain.toUi() = ContactUiModel(
    displayName = displayName,
    lastName = lastName,
    telephone = telephone
)