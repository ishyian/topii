package com.topiichat.app.features.terms

import android.content.Context
import android.content.Intent
import com.topiichat.app.core.platform.BaseActivity


class TermsActivity : BaseActivity() {
    companion object {
        fun callingIntent(context: Context) = Intent(context, TermsActivity::class.java)
    }

    override fun fragment() = TermsFragment()
}