package com.topiichat.app.core.platform

interface IBaseFragment {
    fun onVisibilityLoader(isVisibleLoader: Boolean)
    fun onNavigate(actionId: Int)
}