package com.topiichat.app.features.kyc.email.presentation

import com.topiichat.app.features.kyc.base.presentation.IKYCFragment
import com.topiichat.app.features.kyc.email.domain.OnboardingDomain

interface IEnterEmailFragment : IKYCFragment {
    fun onStartOnboarding(onboardingDomain: OnboardingDomain)
}