package com.topiichat.core.presentation.error

import android.os.Parcelable
import com.topiichat.core.presentation.error.model.Error
import kotlinx.parcelize.Parcelize

@Parcelize
data class ErrorParameters(
    val error: Error
) : Parcelable