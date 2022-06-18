package com.topiichat.app.features.activate_permission

import android.content.Context
import android.content.Intent
import com.topiichat.app.core.platform.BaseActivity


class PermissionActivity : BaseActivity() {
    companion object {
        fun callingIntent(context: Context) = Intent(context, PermissionActivity::class.java)
    }

    override fun fragment() = PermissionFragment()
}