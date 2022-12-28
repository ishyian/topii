package com.topiichat.app.features.new_beneficiary.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.topiichat.app.R
import com.topiichat.app.databinding.FragmentNewBeneficiaryBinding
import com.topiichat.core.extension.showSelectorDialog
import com.topiichat.core.presentation.platform.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewBeneficiaryFragment : BaseFragment<FragmentNewBeneficiaryBinding>(), INewBeneficiaryFragment {

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentNewBeneficiaryBinding.inflate(inflater, container, false)

    override fun onVisibilityLoader(isVisibleLoader: Boolean) = Unit

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        textCountry.editText.isEnabled = false
        textCountry.setOnClickListener {
            val options = listOf("Ukraine", "GT")
            showSelectorDialog(getString(R.string.address_address_hint), options) { _, position ->
                binding.textCountry.text = options[position]
            }
        }
    }
}