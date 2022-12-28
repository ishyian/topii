package com.topiichat.core.presentation.error

import com.topiichat.core.presentation.error.model.ErrorUiModel
import com.topiichat.core.presentation.platform.IBaseFragment

interface IErrorFragment : IBaseFragment {
    fun onErrorModelReceived(errorUiModel: ErrorUiModel)
}