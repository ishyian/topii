package com.topiichat.app.features.error.presentation

import android.os.Parcelable
import com.topiichat.app.features.error.presentation.model.Error
import kotlinx.parcelize.Parcelize

@Parcelize
data class ErrorParameters(
    val error: Error
) : Parcelable