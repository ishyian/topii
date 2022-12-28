package com.topiichat.app.features.contacts.presentation.mapper

import com.topiichat.app.features.contacts.domain.model.ContactDomain
import com.topiichat.app.features.contacts.domain.model.toUi
import com.topiichat.app.features.contacts.presentation.model.ContactLetterUiModel
import com.topiichat.app.features.contacts.presentation.model.ContactsListUiModel
import com.topiichat.core.domain.Mapper
import javax.inject.Inject

class ContactsUiMapper @Inject constructor() : Mapper<List<ContactDomain>, ContactsListUiModel> {
    override fun map(input: List<ContactDomain>?): ContactsListUiModel {
        return if (input.isNullOrEmpty()) ContactsListUiModel(items = emptyList())
        else {
            var letter = ""
            val contactsWithHeaders = arrayListOf<Any>()
            input.forEach {
                val firstLetter = getContactFirstLetter(it)
                if (firstLetter != letter) {
                    letter = firstLetter
                    contactsWithHeaders.add(ContactLetterUiModel(letter))
                }
                contactsWithHeaders.add(it.toUi())
            }
            ContactsListUiModel(items = contactsWithHeaders)
        }
    }

    private fun getContactFirstLetter(contact: ContactDomain): String {
        return if (contact.displayName.isEmpty()) "#"
        else if (contact.displayName[0].isDigit() || contact.displayName[0] == '+') "#"
        else contact.displayName[0].toString()
    }
}