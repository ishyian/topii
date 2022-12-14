package com.topiichat.chat.chat_contacts.presentation

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.topiichat.core.annotations.ChatRouterQualifier
import com.topiichat.core.presentation.platform.BaseViewModel
import com.yourbestigor.chat.R
import dagger.assisted.AssistedInject
import ru.terrakok.cicerone.Router

class ChatContactsViewModel @AssistedInject constructor(
    @ChatRouterQualifier appRouter: Router
) : BaseViewModel(appRouter), IChatContactsViewModel {

    private val _content: MutableLiveData<List<Any>> = MutableLiveData()
    val content: LiveData<List<Any>> = _content

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.image_back -> {
                onClickBack()
            }
        }
    }

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(): ChatContactsViewModel
    }
}