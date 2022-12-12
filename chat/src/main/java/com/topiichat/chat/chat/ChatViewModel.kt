package com.topiichat.chat.chat

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.topiichat.chat.ChatsScreens
import com.topiichat.chat.search.presentation.SearchParameters
import com.topiichat.core.annotations.ChatRouterQualifier
import com.topiichat.core.presentation.platform.BaseViewModel
import com.yourbestigor.chat.R
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