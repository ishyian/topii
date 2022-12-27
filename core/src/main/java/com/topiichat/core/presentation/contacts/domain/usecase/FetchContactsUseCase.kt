package com.topiichat.app.features.contacts.domain.usecase

import com.topiichat.app.features.contacts.domain.model.ContactDomain
import com.topiichat.app.features.contacts.domain.repo.ContactsRepository
import javax.inject.Inject

class FetchContactsUseCase @Inject constructor(private val repository: ContactsRepository) {
    suspend operator fun invoke(query: String? = null): List<ContactDomain> {
        return repository.fetchContacts(query)
    }
}