package com.topiichat.app.features.chats.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import com.topiichat.app.core.presentation.navigation.Navigator
import com.topiichat.app.core.presentation.platform.BaseFragment
import com.topiichat.app.databinding.FragmentChatsBinding

class ChatsFragment : BaseFragment<FragmentChatsBinding>(), IChatsFragment {

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentChatsBinding.inflate(inflater, container, false)

    override fun onVisibilityLoader(isVisibleLoader: Boolean) = Unit

    override fun onNavigate(navigator: Navigator) {
        navigator.navigate(currentActivity.navController)
    }
}