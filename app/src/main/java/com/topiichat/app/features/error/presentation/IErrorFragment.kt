package com.topiichat.app.features.error.presentation

import com.topiichat.app.core.presentation.platform.IBaseFragment
import com.topiichat.app.features.error.presentation.model.ErrorUiModel

interface IErrorFragment : IBaseFragment {
    fun onErrorModelReceived(errorUiModel: ErrorUiModel)
}