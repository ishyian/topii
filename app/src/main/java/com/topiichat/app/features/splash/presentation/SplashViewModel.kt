package com.topiichat.app.features.splash.presentation

import android.view.View
import androidx.lifecycle.viewModelScope
import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.core.presentation.platform.BaseViewModel
import com.topiichat.app.features.MainScreens
import com.topiichat.app.features.chats.ChatsScreens
import com.topiichat.app.features.registration.domain.usecases.FetchAccessTokenUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class SplashViewModel @Inject constructor(
    private val fetchAccessToken: FetchAccessTokenUseCase,
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
        }
    }

    override fun onClick(view: View?) = Unit

    private suspend fun fetchToken() {
        when (val result = fetchAccessToken()) {
            is ResultData.Success -> {
                if (result.data?.token.isNullOrEmpty()) {
                    navigate(ChatsScreens.ChatsList, true)
                } else navigate(MainScreens.Home, true)
            }
            else -> {
                navigate(MainScreens.Terms, true)
            }
        }
    }

    companion object {
        private const val TIME_LOADER = 2000L
    }
}