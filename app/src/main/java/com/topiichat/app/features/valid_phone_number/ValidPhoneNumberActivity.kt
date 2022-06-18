package com.topiichat.app.features.valid_phone_number

import android.content.Context
import android.content.Intent
import com.topiichat.app.core.platform.BaseActivity

class ValidPhoneNumberActivity : BaseActivity() {
    companion object {
        fun callingIntent(context: Context) = Intent(context, ValidPhoneNumberActivity::class.java)
    }

    override fun fragment() = ValidPhoneNumberFragment()
}