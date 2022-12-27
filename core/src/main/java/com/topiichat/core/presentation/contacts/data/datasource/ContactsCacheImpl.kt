package com.topiichat.app.features.contacts.data.datasource

import com.topiichat.core.presentation.contacts.data.datasource.ContactsCache
import contacts.core.Contacts
import contacts.core.entities.Contact
import contacts.permissions.broadQueryWithPermission
import contacts.permissions.queryWithPermission

class ContactsCacheImpl(
    private val contacts: Contacts
) : ContactsCache {
    override suspend fun fetchContacts(): List<Contact> {
        return contacts
            .queryWithPermission()
            .find()
            .filter { contact -> contact.hasPhoneNumber ?: false }
            .sortedBy { contact -> contact.displayNamePrimary }
    }

    override suspend fun searchContacts(query: String): List<Contact> {
        return contacts
            .broadQueryWithPermission()
            .wherePartiallyMatches(query)
            .find()
            .filter { contact -> contact.hasPhoneNumber ?: false }
            .sortedBy { contact -> contact.displayNamePrimary }
    }
}