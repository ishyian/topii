package com.topiichat.app.features.send_payment.presentation

import android.view.View
import com.topiichat.app.R
import com.topiichat.app.core.presentation.platform.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@HiltViewModel
class SendPaymentViewModel @Inject constructor(appRouter: Router) : BaseViewModel(appRouter), ISendPaymentViewModel {

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.image_view_back -> onClickBack()
        }
    }
}