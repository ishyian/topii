package com.topiichat.app.features.contacts.presentation.model

data class ContactUiModel(
    val displayName: String,
    val lastName: String,
    val telephone: String,
    var isSelected: Boolean = false
)