package com.topiichat.app.core.presentation.platform

import com.topiichat.app.core.presentation.navigation.Navigator

interface IBaseFragment {
    fun onVisibilityLoader(isVisibleLoader: Boolean)
    fun onNavigate(navigator: Navigator)
}