package com.topiichat.app.features.chats.chat

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.topiichat.app.R
import com.topiichat.app.core.annotations.ChatRouterQualifier
import com.topiichat.app.core.presentation.platform.BaseViewModel
import com.topiichat.app.features.chats.ChatsScreens
import com.topiichat.app.features.chats.search.presentation.SearchParameters
import dagger.assisted.AssistedInject
import ru.terrakok.cicerone.Router

class ChatViewModel @AssistedInject constructor(
    @ChatRouterQualifier appRouter: Router
) : BaseViewModel(appRouter),
    IChatViewModel {

    private val _onMoreDialogShow: MutableLiveData<Unit> = MutableLiveData()
    val onMoreDialogShow: LiveData<Unit> = _onMoreDialogShow

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.image_more -> onMoreDialogShow()
        }
    }

    override fun onMoreDialogShow() {
        _onMoreDialogShow.value = Unit
    }

    override fun onSearchClick(uuid: String?) {
        navigate(ChatsScreens.Search(SearchParameters(uuid)))
    }

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(): ChatViewModel
    }
}