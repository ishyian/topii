package com.topiichat.remittance.features.new_beneficiary.presentation

import android.view.View
import com.topiichat.core.annotations.ChatRouterQualifier
import com.topiichat.core.presentation.platform.BaseViewModel
import dagger.assisted.AssistedInject
import ru.terrakok.cicerone.Router
import timber.log.Timber

class NewBeneficiaryViewModel @AssistedInject constructor(
    @ChatRouterQualifier appRouter: Router
) : BaseViewModel(appRouter), INewBeneficiaryViewModel {

    override fun onClick(view: View?) {
        when (view?.id) {
            com.topiichat.core.R.id.image_view_back -> {
                Timber.d("onClickBack")
                onClickBack()
            }
        }
    }

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(): NewBeneficiaryViewModel
    }
}