package com.topiichat.app.core.platform

import com.topiichat.app.core.navigation.Navigator

interface IBaseFragment {
    fun onVisibilityLoader(isVisibleLoader: Boolean)
    fun onNavigate(navigator: Navigator)
}