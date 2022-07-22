package com.topiichat.app.features.kyc.personal_data.presentation

import com.topiichat.app.features.kyc.base.presentation.IKYCViewModel
import com.topiichat.app.features.kyc.personal_data.presentation.model.PersonalData

interface IPersonalDataViewModel : IKYCViewModel {
    fun onPersonalDataChanged(type: PersonalData, value: String)
}