package com.topiichat.app.features.chats.base

import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.topiichat.core.presentation.platform.BaseFragment
import eu.siacs.conversations.ui.interfaces.OnBackendConnected

/**
 * Base Fragment class with helper methods for handling views and back button events.
 *
 * @see Fragment
 */

abstract class BaseChatFragment<B : ViewBinding> : BaseFragment<B>(), OnBackendConnected {

    abstract fun refresh()

    fun runOnUiThread(runnable: Runnable?) {
        requireActivity().runOnUiThread(runnable)
    }
}