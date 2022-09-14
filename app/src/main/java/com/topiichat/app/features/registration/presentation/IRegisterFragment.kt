package com.topiichat.app.features.registration.presentation

import com.topiichat.app.core.presentation.platform.IBaseFragment
import com.topiichat.app.features.registration.presentation.model.BtnRegisterEnablingUi

interface IRegisterFragment : IBaseFragment {
    fun onEnablingBtnRegister(btnRegisterEnabling: BtnRegisterEnablingUi)
}