package com.topiichat.app.features.splash.presentation

import android.view.View
import androidx.lifecycle.viewModelScope
import com.topiichat.app.R
import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.core.presentation.navigation.Navigator
import com.topiichat.app.core.presentation.platform.BaseViewModel
import com.topiichat.app.features.splash.domain.usecases.FetchTokenUseCase
import com.topiichat.app.features.splash.presentation.model.Token
import kotlinx.coroutines.delay
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
            delay(TIME_LOADER)
            _showLoader.value = true
            delay(TIME_LOADER)
            val result = fetchToken()
            _navigate.setValue(Navigator(R.id.action_splash_to_send_payment))
        }
        //        val r = fetchTokenUseCase.invoke()
    }

    override fun onClick(view: View?) = Unit

    suspend fun fetchToken() {
        val request = FetchTokenUseCase.Params(isRemote = false)
        when (fetchTokenUseCase(request)) {
            is ResultData.Success -> {
                Token.Success
            }
            is ResultData.Fail -> {
                Token.Fail("")
            }
            is ResultData.NetworkError -> onNetworkError()
        }
    }

    companion object {
        private const val TIME_LOADER = 2000L
    }
}