package com.topiichat.app.features.personal_information.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import com.topiichat.app.databinding.FragmentPersonalInfoBinding
import com.topiichat.app.features.kyc.base.presentation.model.BtnContinueUiState
import com.topiichat.app.features.kyc.personal_data.presentation.model.PersonalData
import com.topiichat.core.extension.viewModelCreator
import com.topiichat.core.presentation.platform.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PersonalInfoFragment : BaseFragment<FragmentPersonalInfoBinding>(), IPersonalInfoFragment {

    @Inject
    lateinit var factory: PersonalInfoViewModel.AssistedFactory
    private val viewModel by viewModelCreator {
        factory.create()
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentPersonalInfoBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        setupClickListener(btnContinue, toolbar.btnBack)

        textName.editText.doAfterTextChanged { text ->
            viewModel.onPersonalDataChanged(PersonalData.NAME, text.toString())
        }
        textSurname.editText.doAfterTextChanged { text ->
            viewModel.onPersonalDataChanged(PersonalData.LAST_NAME, text.toString())
        }
        textSecondName.editText.doAfterTextChanged { text ->
            viewModel.onPersonalDataChanged(PersonalData.SECOND_NAME, text.toString())
        }
        textSecondSurname.editText.doAfterTextChanged { text ->
            viewModel.onPersonalDataChanged(PersonalData.SECOND_LAST_NAME, text.toString())
        }

        initObservers()
    }

    override fun onClick(v: View?) {
        viewModel.onClick(v)
    }

    override fun onVisibilityLoader(isVisibleLoader: Boolean) = Unit

    override fun onBtnContinueUiStateChanged(uiState: BtnContinueUiState) = with(binding.btnContinue) {
        isEnabled = uiState.isEnabled
        setBackgroundResource(uiState.backgroundId)
    }

    private fun initObservers() = with(viewModel) {
        observe(btnContinueUiState, ::onBtnContinueUiStateChanged)
    }
}