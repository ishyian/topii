package com.topiichat.app.features.contacts.data.datasource

import contacts.core.entities.Contact

interface ContactsCache {
    suspend fun fetchContacts(): List<Contact>
    suspend fun searchContacts(query: String): List<Contact>
}