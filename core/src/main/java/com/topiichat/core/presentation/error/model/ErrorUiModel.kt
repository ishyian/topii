package com.topiichat.core.presentation.error.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class ErrorUiModel(
    @StringRes val title: Int,
    @StringRes val subtitle: Int,
    @StringRes val buttonText: Int,
    @DrawableRes val icon: Int
)