package com.topiichat.app.features.home.presentation

import com.topiichat.app.core.presentation.platform.IBaseFragment

interface IHomeFragment : IBaseFragment {
    fun onContentLoaded(content: List<Any>)
}