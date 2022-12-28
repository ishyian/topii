package com.topiichat.chat.chat_list.presentation

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.topiichat.chat.ChatsScreens
import com.topiichat.chat.chat_list.presentation.model.ChatAction
import com.topiichat.chat.chat_list.presentation.model.ChatActionUiModel
import com.topiichat.chat.search.presentation.SearchParameters
import com.topiichat.core.annotations.ChatRouterQualifier
import com.topiichat.core.presentation.platform.BaseViewModel
import com.topiichat.remittance.features.RemittanceScreens
import com.yourbestigor.chat.R
import dagger.assisted.AssistedInject
import ru.terrakok.cicerone.Router

class ChatsViewModel @AssistedInject constructor(
    @ChatRouterQualifier appRouter: Router
) : BaseViewModel(appRouter), IChatsViewModel {

    private val _content: MutableLiveData<List<Any>> = MutableLiveData()
    val content: LiveData<List<Any>> = _content

    private val _startChat: MutableLiveData<Boolean> = MutableLiveData()
    val startChat: LiveData<Boolean> = _startChat

    init {
        _content.value = listOf(
            ChatActionUiModel(
                action = ChatAction.CREATE_GROUP
            ),
            ChatActionUiModel(
                action = ChatAction.SEARCH_BY_PHONE
            ),
            ChatActionUiModel(
                action = ChatAction.INVITE_FRIENDS
            )
        )
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.image_remittance -> {
                onKYCClick()
            }
            R.id.image_back -> {
                onClickBack()
            }
            R.id.edit_text_search -> {
                onSearchClick()
            }
        }
    }

    override fun onSearchClick() {
        navigate(ChatsScreens.Search(SearchParameters()))
    }

    override fun onChatActionClick(action: ChatAction) {
        when (action) {
            ChatAction.INVITE_FRIENDS -> {
                navigate(ChatsScreens.ChatContacts)
            }
            else -> _startChat.postValue(true)
        }
    }

    override fun onKYCClick() {
        navigate(RemittanceScreens.NewBeneficiary)
    }

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(): ChatsViewModel
    }
}