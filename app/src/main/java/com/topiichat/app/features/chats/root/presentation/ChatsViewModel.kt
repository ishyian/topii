package com.topiichat.app.features.chats.root.presentation

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.topiichat.app.R
import com.topiichat.app.core.presentation.platform.BaseViewModel
import com.topiichat.app.features.chats.ChatsScreens
import com.topiichat.app.features.chats.chat.presentation.ChatParameters
import com.topiichat.app.features.chats.root.presentation.model.ChatAction
import com.topiichat.app.features.chats.root.presentation.model.ChatActionUiModel
import com.topiichat.app.features.chats.root.presentation.model.ChatItemUiModel
import com.topiichat.app.features.kyc.KYCScreens
import com.topiichat.app.features.kyc.personal_data.presentation.PersonalDataParameters
import com.topiichat.app.features.registration.domain.usecases.GetAuthDataUseCase
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router

class ChatsViewModel @AssistedInject constructor(
    private val getAuthData: GetAuthDataUseCase,
    appRouter: Router
) : BaseViewModel(appRouter), IChatsViewModel {

    private val _content: MutableLiveData<List<Any>> = MutableLiveData()
    val content: LiveData<List<Any>> = _content

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
            ),
            ChatItemUiModel(
                message = "Hola! Esto es un mensaje",
                from = "Alberto García",
                date = "14:54",
                unreadCount = 3
            ),
            ChatItemUiModel(
                message = "Hola! Esto es un mensaje",
                from = "Robert Fox",
                date = "14:54",
                unreadCount = 0
            ),
            ChatItemUiModel(
                message = "Hola! Esto es un mensaje",
                from = "Leslie Alexander",
                date = "14:54",
                unreadCount = 200
            ),
            ChatItemUiModel(
                message = "Hola! Esto es un mensaje",
                from = "Alberto García",
                date = "14:54",
                unreadCount = 3
            ),
            ChatItemUiModel(
                message = "Hola! Esto es un mensaje",
                from = "Robert Fox",
                date = "14:54",
                unreadCount = 0
            ),
            ChatItemUiModel(
                message = "Hola! Esto es un mensaje",
                from = "Leslie Alexander",
                date = "14:54",
                unreadCount = 200
            ),
            ChatItemUiModel(
                message = "Hola! Esto es un mensaje",
                from = "Alberto García",
                date = "14:54",
                unreadCount = 3
            ),
            ChatItemUiModel(
                message = "Hola! Esto es un mensaje",
                from = "Robert Fox",
                date = "14:54",
                unreadCount = 0
            ),
            ChatItemUiModel(
                message = "Hola! Esto es un mensaje",
                from = "Leslie Alexander",
                date = "14:54",
                unreadCount = 200
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
        }
    }

    override fun onChatItemClick(chatItem: ChatItemUiModel) {
        navigate(ChatsScreens.Chat(ChatParameters(from = chatItem.from)))
    }

    override fun onChatActionClick(action: ChatAction) {
        //TODO: Implement actions
    }

    override fun onKYCClick() {
        viewModelScope.launch {
            val parameters = PersonalDataParameters(getAuthData().isoCode)
            navigate(KYCScreens.PersonalData(parameters))
        }
    }

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(): ChatsViewModel
    }
}