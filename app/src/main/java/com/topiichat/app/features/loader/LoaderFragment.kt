package com.topiichat.app.features.loader

import android.os.Bundle
import android.os.Handler
import android.view.View
import com.topiichat.app.R
import com.topiichat.app.core.navigation.Navigator
import com.topiichat.app.core.platform.BaseFragment
import javax.inject.Inject

class LoaderFragment : BaseFragment() {

    @Inject
    internal lateinit var navigator: Navigator

    override fun layoutId() = R.layout.fragment_loader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loaderStart()

    }

    private fun loaderStart() {
        Handler().postDelayed({
            Handler().run {
                navigator = Navigator()
                navigator.showTerms(requireActivity());
            }

        }, 3000)
    }
}