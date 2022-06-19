package com.topiichat.app.features.splash.presentation

import android.view.View
import androidx.lifecycle.viewModelScope
import com.topiichat.app.R
import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.core.presentation.navigation.Navigator
import com.topiichat.app.core.presentation.platform.BaseViewModel
import com.topiichat.app.features.splash.domain.usecases.FetchTokenUseCase
import com.topiichat.app.features.splash.presentation.model.Token
import kotlinx.coroutines.launch
import javax.inject.Inject

class SplashViewModel @Inject constructor(
    private val fetchTokenUseCase: FetchTokenUseCase
) : BaseViewModel(), ISplashViewModel {

    init {
        onLoaderStart()
    }

    override fun onLoaderStart() {
        _showLoader.value = false
        viewModelScope.launch {
            _showLoader.value = true
            val result = fetchToken()
            _navigate.setValue(Navigator(R.id.action_splash_to_terms))
        }
//        val r = fetchTokenUseCase.invoke()
    }

    override fun onClick(view: View?) = Unit

    suspend fun fetchToken() {
        val request = FetchTokenUseCase.Params(isRemote = false)
        val token = when (val result = fetchTokenUseCase(request)) {
            is ResultData.Success -> {
                Token.Success
            }
            is ResultData.Fail -> {
                Token.Fail("")
            }
        }
    }

    companion object {
        private const val TIME_LOADER = 500L
    }
}