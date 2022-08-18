package com.topiichat.app.features.contacts.presentation

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.topiichat.app.R
import com.topiichat.app.core.presentation.platform.BaseViewModel
import com.topiichat.app.features.contacts.domain.model.ContactDomain
import com.topiichat.app.features.contacts.domain.usecase.FetchContactsUseCase
import com.topiichat.app.features.contacts.presentation.mapper.ContactsUiMapper
import com.topiichat.app.features.contacts.presentation.model.ContactUiModel
import com.topiichat.app.features.contacts.presentation.model.ContactsListUiModel
import com.topiichat.app.features.contacts.presentation.model.changeContactCheckedStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel
@Inject constructor(
    private val fetchContacts: FetchContactsUseCase,
    private val contactsUiMapper: ContactsUiMapper,
    appRouter: Router
) : BaseViewModel(appRouter), IContactsViewModel {

    private val _content: MutableLiveData<ContactsListUiModel> = MutableLiveData()
    val content: LiveData<ContactsListUiModel> = _content

    override fun loadContacts() {
        viewModelScope.launch {
            val contacts = fetchContacts()
            _content.value = contactsUiMapper.map(contacts)
        }
    }

    override fun getContactFirstLetter(contact: ContactDomain): String {
        return if (contact.displayName.isEmpty()) "#"
        else if (contact.displayName[0].isDigit() || contact.displayName[0] == '+') "#"
        else contact.displayName[0].toString()
    }

    override fun onContactClick(item: ContactUiModel) {
        val newModel = content.value?.changeContactCheckedStatus(item)
        _content.value = newModel
    }

    override fun onRemoveContactClick(item: ContactUiModel) {
        val newModel = content.value?.changeContactCheckedStatus(
            item
        )
        _content.value = newModel
    }

    override fun searchContacts(query: String) {
        viewModelScope.launch {
            val contacts = if (query.isEmpty()) fetchContacts() else fetchContacts(query)
            _content.value = contactsUiMapper.map(contacts)
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.image_view_back -> onClickBack()
            R.id.text_next -> onNextClick()
        }
    }

    override fun onNextClick() {
        onClickBack()
    }
}