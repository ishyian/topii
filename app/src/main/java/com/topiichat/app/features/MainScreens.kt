package com.topiichat.app.features

import com.topiichat.app.core.delegates.parcelableParametersBundleOf
import com.topiichat.app.features.activate_permission.presentation.PermissionFragment
import com.topiichat.app.features.home.presentation.HomeFragment
import com.topiichat.app.features.otp.presentation.OtpFragment
import com.topiichat.app.features.otp.presentation.OtpParameters
import com.topiichat.app.features.pin_code.presentation.PinCodeFragment
import com.topiichat.app.features.pin_code.presentation.PinCodeParameters
import com.topiichat.app.features.registration.presentation.RegisterFragment
import com.topiichat.app.features.registration.presentation.RegisterParameters
import com.topiichat.app.features.send_payment.presentation.SendPaymentFragment
import com.topiichat.app.features.splash.presentation.SplashFragment
import com.topiichat.app.features.terms.presentation.TermsFragment
import com.topiichat.app.features.valid_phone_number.presentation.ValidPhoneNumberFragment
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

    object SendPayment : SupportAppScreen() {
        override fun getFragment() = SendPaymentFragment()
    }
}