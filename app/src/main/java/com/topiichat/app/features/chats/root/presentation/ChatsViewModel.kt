package com.topiichat.app.features.chats.root.presentation

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.topiichat.app.R
import com.topiichat.app.core.annotations.ChatRouterQualifier
import com.topiichat.app.features.chats.ChatsScreens
import com.topiichat.app.features.chats.root.presentation.model.ChatAction
import com.topiichat.app.features.chats.root.presentation.model.ChatActionUiModel
import com.topiichat.app.features.chats.search.presentation.SearchParameters
import com.topiichat.app.features.kyc.KYCScreens
import com.topiichat.app.features.kyc.personal_data.presentation.PersonalDataParameters
import com.topiichat.app.features.registration.domain.usecases.GetAuthDataUseCase
import com.topiichat.core.presentation.platform.BaseViewModel
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router

class ChatsViewModel @AssistedInject constructor(
    private val getAuthData: GetAuthDataUseCase,
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
        _startChat.postValue(true)
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