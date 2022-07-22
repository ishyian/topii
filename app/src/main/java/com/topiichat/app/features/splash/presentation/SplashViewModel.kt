package com.topiichat.app.features.splash.presentation

import android.view.View
import androidx.lifecycle.viewModelScope
import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.core.presentation.platform.BaseViewModel
import com.topiichat.app.features.MainScreens
import com.topiichat.app.features.splash.domain.usecases.FetchTokenUseCase
import com.topiichat.app.features.splash.presentation.model.Token
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class SplashViewModel @Inject constructor(
    private val fetchTokenUseCase: FetchTokenUseCase,
    appRouter: Router
) : BaseViewModel(appRouter), ISplashViewModel {

    init {
        onLoaderStart()
    }

    override fun onLoaderStart() {
        _showLoader.value = false
        viewModelScope.launch {
            delay(TIME_LOADER)
            _showLoader.value = true
            delay(TIME_LOADER)
            fetchToken()
            navigate(MainScreens.Terms, true)
        }
    }

    override fun onClick(view: View?) = Unit

    private suspend fun fetchToken() {
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