package com.topiichat.app.features.home.presentation

import com.topiichat.app.features.home.presentation.model.HomeRemittanceHistoryUiModel
import com.topiichat.core.presentation.platform.IBaseFragment

interface IHomeFragment : IBaseFragment {
    fun onContentLoaded(content: HomeRemittanceHistoryUiModel)
}