package com.topiichat.remittance.features

import com.topiichat.remittance.features.new_beneficiary.presentation.NewBeneficiaryFragment
import ru.terrakok.cicerone.android.support.SupportAppScreen

object RemittanceScreens {
    object NewBeneficiary : SupportAppScreen() {
        override fun getFragment() = NewBeneficiaryFragment()
    }
}