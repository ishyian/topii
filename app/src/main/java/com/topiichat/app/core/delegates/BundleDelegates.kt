package com.topiichat.app.core.delegates

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment

const val EXTRA_PARAMETERS = "extra_parameters"

inline fun <reified T : Parcelable> parcelableParametersBundleOf(parameters: T): Bundle {
    return Bundle().apply {
        putParcelable(EXTRA_PARAMETERS, parameters)
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun <T : Parcelable> Fragment.parcelableParameters() = lazy(LazyThreadSafetyMode.NONE) {
    requireNotNull(requireArguments().getParcelable<T>(EXTRA_PARAMETERS))
}

@Suppress("NOTHING_TO_INLINE")
inline fun <T : Parcelable> Fragment.parcelableParameters(defaultValue: T) = lazy(LazyThreadSafetyMode.NONE) {
    arguments?.getParcelable<T>(EXTRA_PARAMETERS) ?: defaultValue
}