package com.topiichat.app.features.splash


import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.topiichat.app.R
import com.topiichat.app.core.navigation.Navigator
import com.topiichat.app.core.platform.BaseFragment
import javax.inject.Inject


class SplashFragment : BaseFragment() {
    @Inject
    internal lateinit var navigator: Navigator

    override fun layoutId() = R.layout.fragment_splash

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
                navigator.showLoader(requireActivity());
            }

        }, 3000)
    }
}