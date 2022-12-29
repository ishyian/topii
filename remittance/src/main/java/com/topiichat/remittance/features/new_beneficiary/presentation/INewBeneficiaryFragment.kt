package com.topiichat.remittance.features.new_beneficiary.presentation

import com.google.android.material.textfield.TextInputLayout
import com.topiichat.core.presentation.platform.IBaseFragment

interface INewBeneficiaryFragment : IBaseFragment {
    fun setupPhoneNumberInputField(inputLayout: TextInputLayout)
}