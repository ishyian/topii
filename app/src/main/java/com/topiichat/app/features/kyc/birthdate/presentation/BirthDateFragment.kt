package com.topiichat.app.features.kyc.birthdate.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.topiichat.app.R
import com.topiichat.app.core.extension.setupDateMask
import com.topiichat.app.core.extension.showSelectorDialog
import com.topiichat.app.core.presentation.platform.BaseFragment
import com.topiichat.app.databinding.FragmentBirthDateBinding
import com.topiichat.app.features.kyc.base.presentation.model.BtnContinueUiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BirthDateFragment : BaseFragment<FragmentBirthDateBinding>(), IBirthDateFragment {

    private val viewModel: BirthDateViewModel by viewModels()

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentBirthDateBinding {
        return FragmentBirthDateBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        editBirthDate.setupDateMask()

        applyBirthDayHint(hasFocus = true)
        editBirthDate.setOnFocusChangeListener { _, hasFocus -> applyBirthDayHint(hasFocus) }
        editBirthDate.doAfterTextChanged { text -> viewModel.onBirthDateChanged(text.toString()) }

        setupClickListener(btnContinue, textBirthPlace, toolbar.btnClose, toolbar.btnBack)
        initObservers()
    }

    private fun applyBirthDayHint(hasFocus: Boolean) {
        if (hasFocus)
            binding.editBirthDate.setHint(R.string.birth_date_hint)
        else
            binding.editBirthDate.hint = ""
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
        observe(viewModel.btnContinueUiState, ::onBtnContinueUiStateChanged)
        observe(viewModel.showBirthPlaceDialog, ::onShowBirthPlaceDialog)
        observe(showMsgError, ::showErrorMessage)
    }

    override fun onShowBirthPlaceDialog(options: List<String>) {
        showSelectorDialog(getString(R.string.birth_date_place), options) { _, position ->
            binding.textBirthPlace.text = options[position]
            viewModel.onBirthPlaceChanged(options[position])
        }
    }
}