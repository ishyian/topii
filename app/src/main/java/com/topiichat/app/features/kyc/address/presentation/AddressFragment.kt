package com.topiichat.app.features.kyc.address.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.topiichat.app.R
import com.topiichat.app.databinding.FragmentAddressBinding
import com.topiichat.app.features.kyc.base.presentation.model.BtnContinueUiState
import com.topiichat.core.extension.showSelectorDialog
import com.topiichat.core.presentation.platform.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddressFragment : BaseFragment<FragmentAddressBinding>(), IAddressFragment {

    private val viewModel: AddressViewModel by viewModels()

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentAddressBinding {
        return FragmentAddressBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        editTextCity.doAfterTextChanged { text -> viewModel.onCityChanged(text.toString()) }
        editTextPostalCode.doAfterTextChanged { text -> viewModel.onPostalCodeChanged(text.toString()) }

        setupClickListener(btnContinue, toolbar.btnBack, toolbar.btnClose, textAddress, textCountry, textRegion)
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

    override fun onShowAddressDialog(options: List<String>) {
        showSelectorDialog(getString(R.string.address_address_hint), options) { _, position ->
            binding.textAddress.text = options[position]
            viewModel.onAddressChanged(options[position])
        }
    }

    override fun onShowCountryDialog(options: List<String>) {
        showSelectorDialog(getString(R.string.address_country_hint), options) { _, position ->
            binding.textCountry.text = options[position]
            viewModel.onCountryChanged(options[position])
        }
    }

    override fun onShowRegionDialog(options: List<String>) {
        showSelectorDialog(getString(R.string.address_region_hint), options) { _, position ->
            binding.textRegion.text = options[position]
            viewModel.onRegionChanged(options[position])
        }
    }

    private fun initObservers() = with(viewModel) {
        observe(viewModel.btnContinueUiState, ::onBtnContinueUiStateChanged)
        observe(viewModel.showAddressDialog, ::onShowAddressDialog)
        observe(viewModel.showCountryDialog, ::onShowCountryDialog)
        observe(viewModel.showRegionDialog, ::onShowRegionDialog)
    }
}