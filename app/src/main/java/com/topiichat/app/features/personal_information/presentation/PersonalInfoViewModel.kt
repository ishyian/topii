package com.topiichat.app.features.personal_information.presentation

import android.view.View
import com.topiichat.core.presentation.platform.BaseViewModel
import dagger.assisted.AssistedInject
import ru.terrakok.cicerone.Router

class PersonalInfoViewModel @AssistedInject constructor(
    appRouter: Router
) : BaseViewModel(appRouter), IPersonalInfoViewModel {

    override fun onClick(view: View?) {

    }
}