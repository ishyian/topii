package com.topiichat.app.features.remittance_error.presentation

import android.view.View
import com.topiichat.app.R
import com.topiichat.app.core.presentation.platform.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@HiltViewModel
class RemittanceErrorViewModel @Inject constructor(
    appRouter: Router
) : BaseViewModel(appRouter), IRemittanceErrorViewModel {

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.image_back -> onClickBack()
            R.id.btn_return -> onClickBack()
        }
    }
}