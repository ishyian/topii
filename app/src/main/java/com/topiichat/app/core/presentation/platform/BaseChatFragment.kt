package com.topiichat.app.core.presentation.platform

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import com.topiichat.app.AppActivity
import eu.siacs.conversations.ui.BaseXmppFragment

/**
 * Base Fragment class with helper methods for handling views and back button events.
 *
 * @see Fragment
 */
abstract class BaseChatFragment<B : ViewBinding> : BaseXmppFragment(), View.OnClickListener {

    abstract fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): B

    private var _binding: B? = null
    val binding: B get() = _binding ?: error("binding exception")

    protected val currentActivity get() = requireActivity() as AppActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = initBinding(inflater, container)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onClick(v: View?) = Unit

    protected fun setupClickListener(vararg clickIds: View) {
        clickIds.forEach { view ->
            view.setOnClickListener(this)
        }
    }

    protected fun setupClickListener(action: () -> Unit, vararg clickIds: View) {
        action()
        clickIds.forEach { view ->
            view.setOnClickListener(this)
        }
    }

    protected inline fun <T> LifecycleOwner.observe(
        liveData: LiveData<T>,
        crossinline action: (t: T) -> Unit
    ) {
        liveData.observe(this, Observer {
            it?.let { t ->
                action(t)
            }
        })
    }

    protected fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    protected fun showErrorMessage(message: String) {
        showToast(message)
    }
}
