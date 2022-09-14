package com.topiichat.app.features.splash.presentation

import com.topiichat.app.core.domain.ResultData

interface ISplashViewModel {
    fun onLoaderStart()
    suspend fun onFailKYCStatus(result: ResultData.Fail)
}