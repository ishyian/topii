package com.topiichat.app.features.splash

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.topiichat.app.core.platform.BaseActivity


@SuppressLint("CustomSplashScreen")
class SplashActivity: BaseActivity() {
    companion object {
        fun callingIntent(context: Context) = Intent(context, SplashActivity::class.java)
    }


    override fun fragment() = SplashFragment()
}
