package com.topiichat.app.features.contacts.domain.repo

import com.topiichat.app.features.contacts.domain.model.ContactDomain

interface ContactsRepository {
    suspend fun fetchContacts(query: String? = null): List<ContactDomain>
}