package com.topiichat.remittance.features.new_beneficiary.presentation

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.topiichat.core.extension.showSelectorDialog
import com.topiichat.core.presentation.platform.BaseFragment
import com.topiichat.remittance.R
import com.topiichat.remittance.databinding.FragmentNewBeneficiaryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewBeneficiaryFragment : BaseFragment<FragmentNewBeneficiaryBinding>(), INewBeneficiaryFragment {

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentNewBeneficiaryBinding.inflate(inflater, container, false)

    override fun onVisibilityLoader(isVisibleLoader: Boolean) = Unit

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        textCountry.editText.inputType = InputType.TYPE_NULL
        textCountry.setOnClickListener {
            val options = listOf("Ukraine", "GT")
            showSelectorDialog(getString(R.string.new_beneficiary_country_hint), options) { _, position ->
                binding.textCountry.text = options[position]
            }
        }
    }
}