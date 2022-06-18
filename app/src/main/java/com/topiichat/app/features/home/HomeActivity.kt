package com.topiichat.app.features.home

import android.content.Context
import android.content.Intent
import com.topiichat.app.core.platform.BaseActivity


class HomeActivity : BaseActivity() {
    companion object {
        fun callingIntent(context: Context) = Intent(context, HomeActivity::class.java)
    }


    override fun fragment() = HomeFragment()
}