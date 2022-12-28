package com.topiichat.app.features.wallet.card_success.presentation

import android.view.View
import com.topiichat.app.R
import com.topiichat.core.presentation.platform.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@HiltViewModel
class CardAddedViewModel @Inject constructor(
    appRouter: Router
) : BaseViewModel(appRouter), ICardAddedViewModel {

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.image_back -> onClickBack()
            R.id.btn_ok -> onClickBack()
        }
    }
}