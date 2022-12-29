package com.topiichat.remittance.features.new_beneficiary.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.topiichat.core.constants.Constants
import com.topiichat.core.extension.viewModelCreator
import com.topiichat.core.presentation.platform.BaseFragment
import com.topiichat.remittance.databinding.FragmentNewBeneficiaryBinding
import dagger.hilt.android.AndroidEntryPoint
import me.ibrahimsn.lib.PhoneNumberKit
import javax.inject.Inject

@AndroidEntryPoint
class NewBeneficiaryFragment : BaseFragment<FragmentNewBeneficiaryBinding>(), INewBeneficiaryFragment {

    @Inject
    lateinit var factory: NewBeneficiaryViewModel.AssistedFactory

    private val viewModel by viewModelCreator {
        factory.create()
    }

    private val phoneNumberKit by lazy {
        PhoneNumberKit.Builder(requireContext())
            .setIconEnabled(true)
            .build()
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentNewBeneficiaryBinding.inflate(inflater, container, false)

    override fun onVisibilityLoader(isVisibleLoader: Boolean) = Unit

    override fun onClick(v: View?) {
        viewModel.onClick(v)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        setupClickListener(toolbar.btnBack)
        setupPhoneNumberInputField(layoutEditPhoneNumber)
        textCountry.setupValues(listOf("Guatemala", "Republica Dominicana"))
        textCountry.setValueChangedListener {
            binding.textCountry.text = it
        }
    }

    override fun setupPhoneNumberInputField(inputLayout: TextInputLayout) {
        phoneNumberKit.attachToInput(inputLayout, Constants.INITIAL_COUNTRY_ISO_CODE)
        phoneNumberKit.setupCountryPicker(
            activity = requireActivity() as AppCompatActivity,
            searchEnabled = true
        )
    }
}