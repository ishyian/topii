package com.topiichat.app.features

import android.content.Context
import android.content.Intent
import com.topiichat.app.features.activate_permission.presentation.PermissionFragment
import com.topiichat.app.features.home.presentation.HomeFragment
import com.topiichat.app.features.new_beneficiary.presentation.NewBeneficiaryFragment
import com.topiichat.app.features.otp.presentation.OtpFragment
import com.topiichat.app.features.otp.presentation.OtpParameters
import com.topiichat.app.features.pin_code.presentation.PinCodeFragment
import com.topiichat.app.features.pin_code.presentation.PinCodeParameters
import com.topiichat.app.features.registration.presentation.RegisterFragment
import com.topiichat.app.features.registration.presentation.RegisterParameters
import com.topiichat.app.features.remittance.presentation.RemittanceDetailFragment
import com.topiichat.app.features.remittance.presentation.RemittanceParameters
import com.topiichat.app.features.remittance_error.presentation.RemittanceErrorFragment
import com.topiichat.app.features.request_remittance.presentation.RequestRemittanceFragment
import com.topiichat.app.features.request_remittance.presentation.RequestRemittanceParameters
import com.topiichat.app.features.send_remittance.presentation.SendRemittanceFragment
import com.topiichat.app.features.send_remittance.presentation.SendRemittanceParameters
import com.topiichat.app.features.splash.presentation.SplashFragment
import com.topiichat.app.features.terms.presentation.TermsFragment
import com.topiichat.app.features.valid_phone_number.presentation.ValidPhoneNumberFragment
import com.topiichat.chat.activity.ChatsActivity
import com.topiichat.core.delegates.parcelableParametersBundleOf
import com.topiichat.core.presentation.contacts.presentation.ContactsFragment
import com.topiichat.core.presentation.contacts.presentation.ContactsParameters
import ru.terrakok.cicerone.android.support.FragmentParams
import ru.terrakok.cicerone.android.support.SupportAppScreen

object MainScreens {
    object Splash : SupportAppScreen() {
        override fun getFragment() = SplashFragment()
    }

    object Terms : SupportAppScreen() {
        override fun getFragment() = TermsFragment()
    }

    object Permission : SupportAppScreen() {
        override fun getFragment() = PermissionFragment()
    }

    object ValidPhoneNumber : SupportAppScreen() {
        override fun getFragment() = ValidPhoneNumberFragment()
    }

    class Otp(
        private val parameters: OtpParameters
    ) : SupportAppScreen() {
        override fun getFragment() = OtpFragment()
        override fun getFragmentParams() = FragmentParams(
            OtpFragment::class.java,
            parcelableParametersBundleOf(parameters)
        )
    }

    class PinCode(
        private val parameters: PinCodeParameters
    ) : SupportAppScreen() {
        override fun getFragment() = PinCodeFragment()
        override fun getFragmentParams() = FragmentParams(
            PinCodeFragment::class.java,
            parcelableParametersBundleOf(parameters)
        )
    }

    class Register(
        private val parameters: RegisterParameters
    ) : SupportAppScreen() {
        override fun getFragment() = RegisterFragment()
        override fun getFragmentParams() = FragmentParams(
            RegisterFragment::class.java,
            parcelableParametersBundleOf(parameters)
        )
    }

    object Home : SupportAppScreen() {
        override fun getFragment() = HomeFragment()
    }

    class SendRemittance(
        private val parameters: SendRemittanceParameters
    ) : SupportAppScreen() {
        override fun getFragment() = SendRemittanceFragment()
        override fun getFragmentParams() = FragmentParams(
            SendRemittanceFragment::class.java,
            parcelableParametersBundleOf(parameters)
        )
    }

    class Contacts(
        private val parameters: ContactsParameters
    ) : SupportAppScreen() {
        override fun getFragment() = ContactsFragment()
        override fun getFragmentParams() = FragmentParams(
            ContactsFragment::class.java,
            parcelableParametersBundleOf(parameters)
        )
    }

    class RemittanceDetail(
        private val parameters: RemittanceParameters
    ) : SupportAppScreen() {
        override fun getFragment() = RemittanceDetailFragment()
        override fun getFragmentParams() = FragmentParams(
            RemittanceDetailFragment::class.java,
            parcelableParametersBundleOf(parameters)
        )
    }

    object RemittanceError : SupportAppScreen() {
        override fun getFragment() = RemittanceErrorFragment()
    }

    class RequestRemittance(
        private val parameters: RequestRemittanceParameters
    ) : SupportAppScreen() {
        override fun getFragment() = RequestRemittanceFragment()
        override fun getFragmentParams() = FragmentParams(
            RequestRemittanceFragment::class.java,
            parcelableParametersBundleOf(parameters)
        )
    }

    object WelcomeConversations : SupportAppScreen() {
        override fun getActivityIntent(context: Context): Intent {
            return Intent(context, ChatsActivity::class.java)
        }
    }

    object NewBeneficiary : SupportAppScreen() {
        override fun getFragment() = NewBeneficiaryFragment()
    }
}