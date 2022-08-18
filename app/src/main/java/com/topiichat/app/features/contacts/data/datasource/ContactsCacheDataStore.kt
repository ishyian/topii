package com.topiichat.app.features.contacts.data.datasource

import com.topiichat.app.core.data.datasource.BaseCacheDataStore
import contacts.core.entities.Contact
import javax.inject.Inject

class ContactsCacheDataStore @Inject constructor(
    private val contactsCache: ContactsCache
) : BaseCacheDataStore() {

    suspend fun fetchContacts(): List<Contact> {
        return contactsCache.fetchContacts()
    }

    suspend fun searchContacts(query: String): List<Contact> {
        return contactsCache.searchContacts(query)
    }
}