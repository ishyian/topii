package com.topiichat.core.presentation.contacts.data.repo

import com.topiichat.app.features.contacts.domain.model.ContactDomain
import com.topiichat.app.features.contacts.domain.repo.ContactsRepository
import com.topiichat.core.coroutines.AppDispatchers
import com.topiichat.core.presentation.contacts.data.datasource.ContactsCacheDataStore
import com.topiichat.core.presentation.contacts.data.mapper.ContactsCacheMapper
import kotlinx.coroutines.withContext

class ContactsRepositoryImpl(
    private val contactsCacheDataStore: ContactsCacheDataStore,
    private val contactsCacheMapper: ContactsCacheMapper,
    private val appDispatchers: AppDispatchers
) : ContactsRepository {
    override suspend fun fetchContacts(query: String?): List<ContactDomain> {
        return withContext(appDispatchers.storage) {
            if (query == null) {
                contactsCacheDataStore.fetchContacts().map { contact ->
                    contactsCacheMapper.map(contact)
                }
            } else {
                contactsCacheDataStore.searchContacts(query).map { contact ->
                    contactsCacheMapper.map(contact)
                }
            }
        }
    }
}