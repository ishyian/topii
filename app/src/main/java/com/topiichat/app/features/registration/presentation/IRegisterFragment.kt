package com.topiichat.app.features.registration.presentation

import com.topiichat.app.features.registration.presentation.model.BtnRegisterEnablingUi
import com.topiichat.core.presentation.platform.IBaseFragment

interface IRegisterFragment : IBaseFragment {
    fun onEnablingBtnRegister(btnRegisterEnabling: BtnRegisterEnablingUi)
}