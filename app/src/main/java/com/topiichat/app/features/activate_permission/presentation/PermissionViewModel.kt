package com.topiichat.app.features.activate_permission.presentation

import android.view.View
import com.topiichat.app.R
import com.topiichat.app.features.MainScreens
import com.topiichat.core.presentation.platform.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@HiltViewModel
class PermissionViewModel @Inject constructor(
    appRouter: Router
) : BaseViewModel(appRouter), IPermissionViewModel {

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.next_after_permission -> {
                onClickNextAfterPermission()
            }
            R.id.image_view_back -> {
                onClickBack()
            }
            R.id.image_view_close -> {
                onClickClose()
            }
        }
    }

    override fun onClickNextAfterPermission() {
        navigate(MainScreens.ValidPhoneNumber)
    }
}