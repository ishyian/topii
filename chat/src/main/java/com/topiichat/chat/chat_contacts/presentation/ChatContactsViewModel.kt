package com.topiichat.chat.chat_contacts.presentation

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.topiichat.chat.chat_contacts.presentation.model.ChatContactUiModel
import com.topiichat.core.annotations.ChatRouterQualifier
import com.topiichat.core.presentation.platform.BaseViewModel
import com.topiichat.core.presentation.platform.SingleLiveData
import com.yourbestigor.chat.R
import dagger.assisted.AssistedInject
import eu.siacs.conversations.entities.Account
import eu.siacs.conversations.entities.Contact
import eu.siacs.conversations.services.XmppConnectionService
import ru.terrakok.cicerone.Router
import timber.log.Timber

class ChatContactsViewModel @AssistedInject constructor(
    @ChatRouterQualifier appRouter: Router
) : BaseViewModel(appRouter), IChatContactsViewModel {

    private val _content: MutableLiveData<List<Any>> = MutableLiveData()
    val content: LiveData<List<Any>> = _content

    val openChat: SingleLiveData<Contact> = SingleLiveData()

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.image_back -> {
                onClickBack()
            }
        }
    }

    override fun loadContacts(xmppConnectionService: XmppConnectionService) {
        val contacts = arrayListOf<ChatContactUiModel>()
        val accounts: List<Account> = xmppConnectionService.accounts
        for (account in accounts) {
            if (account.status != Account.State.DISABLED) {
                for (contact in account.roster.contacts) {
                    contacts.add(ChatContactUiModel(contact))
                }
            }
        }
        contacts.sortBy { it.contact.displayName }
        _content.postValue(contacts)
        Timber.d(contacts.toString())
    }

    override fun onChatContactClick(item: ChatContactUiModel) {
        openChat.postValue(item.contact)
    }

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(): ChatContactsViewModel
    }
}