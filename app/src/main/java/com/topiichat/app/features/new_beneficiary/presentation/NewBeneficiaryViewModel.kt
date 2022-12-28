package com.topiichat.app.features.new_beneficiary.presentation

import android.view.View
import com.topiichat.core.presentation.platform.BaseViewModel
import dagger.assisted.AssistedInject
import ru.terrakok.cicerone.Router

class NewBeneficiaryViewModel @AssistedInject constructor(
    appRouter: Router
) : BaseViewModel(appRouter), INewBeneficiaryViewModel {

    override fun onClick(view: View?) {

    }
}