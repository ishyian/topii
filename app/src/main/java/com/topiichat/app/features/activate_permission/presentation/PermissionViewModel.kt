package com.topiichat.app.features.activate_permission.presentation

import android.view.View
import com.topiichat.app.R
import com.topiichat.app.core.presentation.navigation.Navigator
import com.topiichat.app.core.presentation.platform.BaseViewModel

class PermissionViewModel : BaseViewModel(), IPermissionViewModel {

    override fun onClick(view: View?) {
        when(view?.id) {
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
        _navigate.setValue(Navigator(
            actionId = R.id.action_permission_to_validPhoneNumber
        ))
    }
}