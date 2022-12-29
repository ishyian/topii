package com.topiichat.app.features.personal_information.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.topiichat.app.databinding.FragmentPersonalInfoBinding
import com.topiichat.core.presentation.platform.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PersonalInfoFragment : BaseFragment<FragmentPersonalInfoBinding>() {
    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentPersonalInfoBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }
}