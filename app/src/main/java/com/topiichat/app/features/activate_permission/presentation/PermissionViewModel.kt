package com.topiichat.app.features.activate_permission.presentation

import android.view.View
import com.topiichat.app.R
import com.topiichat.app.core.platform.BaseViewModel

class PermissionViewModel : BaseViewModel(), IPermissionViewModel {

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.next_after_permission -> {
                onClickNextAfterPermission()
            }
        }
    }

    override fun onClickNextAfterPermission() {
        _navigate.value = R.id.action_permission_to_validPhoneNumber
    }
}