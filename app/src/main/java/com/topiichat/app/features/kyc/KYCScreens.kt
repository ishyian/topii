package com.topiichat.app.features.kyc

import com.topiichat.app.features.kyc.address.presentation.AddressFragment
import com.topiichat.app.features.kyc.birthdate.presentation.BirthDateFragment
import com.topiichat.app.features.kyc.camera_verification.IdentityVerificationFragment
import com.topiichat.app.features.kyc.document.presentation.DocumentFragment
import com.topiichat.app.features.kyc.email.presentation.EnterEmailFragment
import com.topiichat.app.features.kyc.personal_data.presentation.PersonalDataFragment
import ru.terrakok.cicerone.android.support.SupportAppScreen

object KYCScreens {
    object PersonalData : SupportAppScreen() {
        override fun getFragment() = PersonalDataFragment()
    }

    object EnterEmail : SupportAppScreen() {
        override fun getFragment() = EnterEmailFragment()
    }

    object Document : SupportAppScreen() {
        override fun getFragment() = DocumentFragment()
    }

    object BirthDate : SupportAppScreen() {
        override fun getFragment() = BirthDateFragment()
    }

    object Address : SupportAppScreen() {
        override fun getFragment() = AddressFragment()
    }

    object IdentityVerification : SupportAppScreen() {
        override fun getFragment() = IdentityVerificationFragment()
    }
}