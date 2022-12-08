package com.topiichat.app.features.splash.presentation

import com.topiichat.core.domain.ResultData

interface ISplashViewModel {
    fun onLoaderStart()
    suspend fun onFailKYCStatus(result: ResultData.Fail)
}