package com.topiichat.app.features.registration.presentation

import com.topiichat.app.features.registration.domain.model.RegisterDomain
import com.topiichat.core.domain.ResultData
import com.topiichat.core.exception.domain.ErrorDomain

interface IRegisterViewModel {
    fun onCheckedChanged(id: Int?, isChecked: Boolean)
    fun onUpdateBtnRegister()
    fun onRegister()
    fun onRenderRegister(result: ResultData<RegisterDomain>)
    fun onFailRegister(error: ErrorDomain)
    fun onSuccessRegister(accessToken: String, senderId: String)
    fun onKYCStatusNotVerified()
    fun onRenderKYCRegister(result: ResultData<RegisterDomain>)
    fun onSuccessKYCRegister(accessToken: String, senderId: String)
    fun onRegisterKYC()
}