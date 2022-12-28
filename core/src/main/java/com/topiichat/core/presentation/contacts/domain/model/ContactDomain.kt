package com.topiichat.app.features.contacts.domain.model

import com.topiichat.app.features.contacts.presentation.model.ContactUiModel
import com.topiichat.core.domain.Domain

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