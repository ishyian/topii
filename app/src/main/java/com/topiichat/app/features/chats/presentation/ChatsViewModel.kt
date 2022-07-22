package com.topiichat.app.features.chats.presentation

import android.view.View
import com.topiichat.app.R
import com.topiichat.app.core.presentation.platform.BaseViewModel
import com.topiichat.app.features.kyc.KYCScreens
import dagger.assisted.AssistedInject
import ru.terrakok.cicerone.Router

class ChatsViewModel @AssistedInject constructor(
    appRouter: Router
) : BaseViewModel(appRouter), IChatsViewModel {

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.image_kyc -> {
                onKYCClick()
            }
        }
    }

    override fun onKYCClick() {
        navigate(KYCScreens.PersonalData)
    }

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(): ChatsViewModel
    }
}