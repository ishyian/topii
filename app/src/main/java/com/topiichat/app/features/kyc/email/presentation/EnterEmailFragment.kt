package com.topiichat.app.features.kyc.email.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.alicebiometrics.onboarding.api.DocumentType
import com.alicebiometrics.onboarding.api.Onboarding
import com.alicebiometrics.onboarding.config.OnboardingConfig
import com.topiichat.app.AppActivity
import com.topiichat.app.databinding.FragmentEnterEmailBinding
import com.topiichat.app.features.kyc.base.presentation.model.BtnContinueUiState
import com.topiichat.app.features.kyc.email.data.AliceSdkListener
import com.topiichat.app.features.kyc.email.domain.OnboardingDomain
import com.topiichat.core.delegates.parcelableParameters
import com.topiichat.core.extension.viewModelCreator
import com.topiichat.core.presentation.platform.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.ExperimentalSerializationApi
import javax.inject.Inject

@ExperimentalSerializationApi
@AndroidEntryPoint
class EnterEmailFragment : BaseFragment<FragmentEnterEmailBinding>(), IEnterEmailFragment, AliceSdkListener {

    @Inject
    lateinit var factory: EnterEmailViewModel.AssistedFactory
    private val viewModel by viewModelCreator {
        factory.create(parameters)
    }
    private val parameters: EnterEmailParameters by parcelableParameters()

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentEnterEmailBinding {
        return FragmentEnterEmailBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        editTextEmail.doAfterTextChanged { text ->
            viewModel.onEmailChanged(text.toString())
        }
        setupClickListener(btnContinue, toolbar.btnClose, toolbar.btnBack)
        initObservers()
    }

    override fun onClick(v: View?) {
        viewModel.onClick(v)
    }

    override fun onVisibilityLoader(isVisibleLoader: Boolean) = with(binding) {
        groupContent.isVisible = isVisibleLoader.not()
        progressBar.isVisible = isVisibleLoader
    }

    override fun onBtnContinueUiStateChanged(uiState: BtnContinueUiState) = with(binding.btnContinue) {
        isEnabled = uiState.isEnabled
        setBackgroundResource(uiState.backgroundId)
    }

    override fun onStartOnboarding(onboardingDomain: OnboardingDomain) {
        val config = OnboardingConfig.builder()
            .withUserToken(onboardingDomain.tokenAlice)
            .withAddSelfieStage()

        if (onboardingDomain.iso3CountryCode != null) {
            config.withAddDocumentStage(DocumentType.IDCARD, onboardingDomain.iso3CountryCode)
        } else {
            config.withAddDocumentStage(DocumentType.IDCARD)
        }

        val onboarding = Onboarding(requireActivity() as AppActivity, config = config)
        onboarding.run(ONBOARDING_CODE)

    }

    override fun onVerificationSuccess(userId: String) {
        viewModel.onSuccessVerification(userId)
    }

    private fun initObservers() = with(viewModel) {
        observe(showLoader, ::onVisibilityLoader)
        observe(btnContinueUiState, ::onBtnContinueUiStateChanged)
        observe(startOnboarding, ::onStartOnboarding)
    }

    companion object {
        const val ONBOARDING_CODE = 34587
    }
}