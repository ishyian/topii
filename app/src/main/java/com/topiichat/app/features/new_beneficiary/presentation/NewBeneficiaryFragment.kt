package com.topiichat.app.features.new_beneficiary.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import com.topiichat.app.databinding.FragmentNewBeneficiaryBinding
import com.topiichat.core.presentation.platform.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewBeneficiaryFragment : BaseFragment<FragmentNewBeneficiaryBinding>(), INewBeneficiaryFragment {

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentNewBeneficiaryBinding.inflate(inflater, container, false)

    override fun onVisibilityLoader(isVisibleLoader: Boolean) = Unit
}