package com.topiichat.app.features.otp

import android.content.Context
import android.content.Intent
import com.topiichat.app.core.platform.BaseActivity

class OtpActivity : BaseActivity() {
    companion object {
        fun callingIntent(context: Context) = Intent(context, OtpActivity::class.java)
    }

    override fun fragment() = OtpFragment()
}