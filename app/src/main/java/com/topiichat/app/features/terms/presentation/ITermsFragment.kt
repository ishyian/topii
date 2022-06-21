package com.topiichat.app.features.terms.presentation

import android.net.Uri
import com.topiichat.app.core.presentation.platform.IBaseFragment

interface ITermsFragment : IBaseFragment {
    fun onGoActionView(uri: Uri)
}