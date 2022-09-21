package com.topiichat.app.features.kyc

import com.topiichat.app.core.delegates.parcelableParametersBundleOf
import com.topiichat.app.features.kyc.address.presentation.AddressFragment
import com.topiichat.app.features.kyc.birthdate.presentation.BirthDateFragment
import com.topiichat.app.features.kyc.camera_verification.IdentityVerificationFragment
import com.topiichat.app.features.kyc.document.presentation.DocumentFragment
import com.topiichat.app.features.kyc.email.presentation.EnterEmailFragment
import com.topiichat.app.features.kyc.email.presentation.EnterEmailParameters
import com.topiichat.app.features.kyc.personal_data.presentation.PersonalDataFragment
import com.topiichat.app.features.kyc.personal_data.presentation.PersonalDataParameters
import kotlinx.serialization.ExperimentalSerializationApi
import ru.terrakok.cicerone.android.support.FragmentParams
import ru.terrakok.cicerone.android.support.SupportAppScreen

object KYCScreens {
    class PersonalData(
        private val parameters: PersonalDataParameters
    ) : SupportAppScreen() {
        override fun getFragment() = PersonalDataFragment()
        override fun getFragmentParams() = FragmentParams(
            PersonalDataFragment::class.java,
            parcelableParametersBundleOf(parameters)
        )
    }

    @ExperimentalSerializationApi
    class EnterEmail(
        private val parameters: EnterEmailParameters
    ) : SupportAppScreen() {
        override fun getFragment() = EnterEmailFragment()
        override fun getFragmentParams() = FragmentParams(
            EnterEmailFragment::class.java,
            parcelableParametersBundleOf(parameters)
        )
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