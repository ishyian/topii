package com.topiichat.app.features.splash.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.topiichat.app.core.presentation.navigation.Navigator
import com.topiichat.app.core.presentation.platform.BaseFragment
import com.topiichat.app.databinding.FragmentSplashBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>(), ISplashFragment {

    @Inject
    internal lateinit var factory: SplashViewModelFactory
    private val viewModel: SplashViewModel by viewModels { factory }

    override val tagId: Int get() = TODO("Not yet implemented")

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSplashBinding {
        return FragmentSplashBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initObservers()
    }

    private fun initObservers() = with(viewModel) {
        observe(showLoader, ::onVisibilityLoader)
        observe(navigate, ::onNavigate)
    }

    override fun onVisibilityLoader(
        isVisibleLoader: Boolean
    ) = with(binding) {
        groupContent.isVisible = isVisibleLoader.not()
        progressBar.isVisible = isVisibleLoader
    }

    override fun onNavigate(navigator: Navigator) {
        navigator.navigate(currentActivity.navController)
    }
}