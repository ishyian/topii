package com.topiichat.core.presentation.error

import com.topiichat.core.delegates.parcelableParametersBundleOf
import ru.terrakok.cicerone.android.support.FragmentParams
import ru.terrakok.cicerone.android.support.SupportAppScreen

object ErrorScreens {
    class Error(
        private val parameters: ErrorParameters
    ) : SupportAppScreen() {
        override fun getFragment() = ErrorFragment()
        override fun getFragmentParams() = FragmentParams(
            ErrorFragment::class.java,
            parcelableParametersBundleOf(parameters)
        )
    }
}