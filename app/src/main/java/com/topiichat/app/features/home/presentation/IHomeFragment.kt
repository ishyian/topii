package com.topiichat.app.features.home.presentation

import com.topiichat.app.core.presentation.platform.IBaseFragment
import com.topiichat.app.features.home.presentation.model.HomeRemittanceHistoryUiModel

interface IHomeFragment : IBaseFragment {
    fun onContentLoaded(content: HomeRemittanceHistoryUiModel)
}