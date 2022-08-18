package com.topiichat.app.features.contacts.presentation

import com.topiichat.app.features.contacts.domain.model.ContactDomain
import com.topiichat.app.features.contacts.presentation.model.ContactUiModel

interface IContactsViewModel {
    fun loadContacts()
    fun getContactFirstLetter(contact: ContactDomain): String
    fun onContactClick(item: ContactUiModel)
    fun onRemoveContactClick(item: ContactUiModel)
    fun searchContacts(query: String)
    fun onNextClick()
}