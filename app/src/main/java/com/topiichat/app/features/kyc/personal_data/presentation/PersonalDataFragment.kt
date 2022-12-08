package com.topiichat.app.features.kyc.personal_data.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import com.topiichat.app.databinding.FragmentPersonalDataBinding
import com.topiichat.app.features.kyc.base.presentation.model.BtnContinueUiState
import com.topiichat.app.features.kyc.personal_data.presentation.model.PersonalData
import com.topiichat.core.delegates.parcelableParameters
import com.topiichat.core.extension.viewModelCreator
import com.topiichat.core.presentation.platform.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PersonalDataFragment : BaseFragment<FragmentPersonalDataBinding>(), IPersonalDataFragment {

    @Inject
    lateinit var factory: PersonalDataViewModel.AssistedFactory
    private val viewModel by viewModelCreator {
        factory.create(parameters)
    }
    private val parameters: PersonalDataParameters by parcelableParameters()

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentPersonalDataBinding {
        return FragmentPersonalDataBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        textName.editText.doAfterTextChanged { text ->
            viewModel.onPersonalDataChanged(PersonalData.NAME, text.toString())
        }
        textLastName.editText.doAfterTextChanged { text ->
            viewModel.onPersonalDataChanged(PersonalData.LAST_NAME, text.toString())
        }
        textSecondLastName.editText.doAfterTextChanged { text ->
            viewModel.onPersonalDataChanged(PersonalData.SECOND_NAME, text.toString())
        }
        setupClickListener(btnContinue, toolbar.btnClose, toolbar.btnBack)
        initObservers()
    }

    override fun onClick(v: View?) {
        viewModel.onClick(v)
    }

    override fun onVisibilityLoader(isVisibleLoader: Boolean) = Unit

    private fun initObservers() = with(viewModel) {
        observe(viewModel.btnContinueUiState, ::onBtnContinueUiStateChanged)
    }

    override fun onBtnContinueUiStateChanged(uiState: BtnContinueUiState) = with(binding.btnContinue) {
        isEnabled = uiState.isEnabled
        setBackgroundResource(uiState.backgroundId)
    }
}