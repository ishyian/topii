package com.topiichat.app.features.signup

import android.content.Context
import android.content.Intent
import com.topiichat.app.core.platform.BaseActivity


class SignupActivity : BaseActivity() {
    companion object {
        fun callingIntent(context: Context) = Intent(context, SignupActivity::class.java)
    }

    override fun fragment() = SignupFragment()
}