package com.topiichat.app.features.personal_information.presentation

import com.topiichat.app.features.kyc.personal_data.presentation.model.PersonalData
import com.topiichat.core.presentation.platform.IBaseViewModel

interface IPersonalInfoViewModel : IBaseViewModel {
    fun onUpdateContinueBtn()
    fun onPersonalDataChanged(type: PersonalData, value: String)
    fun onContinueBtnClick()
}