package com.topiichat.core.presentation.contacts.data.mapper

import com.topiichat.app.features.contacts.domain.model.ContactDomain
import com.topiichat.core.domain.Mapper
import contacts.core.entities.Contact
import javax.inject.Inject

class ContactsCacheMapper @Inject constructor() : Mapper<Contact, ContactDomain> {
    override fun map(input: Contact?): ContactDomain {
        val rawContact = input?.rawContacts?.first()
        return with(rawContact) {
            val phoneNumber = this?.phones?.first()?.normalizedNumber ?: ""
            ContactDomain(
                displayName = this?.name?.displayName ?: phoneNumber,
                lastName = this?.name?.familyName ?: "",
                telephone = phoneNumber
            )
        }
    }
}