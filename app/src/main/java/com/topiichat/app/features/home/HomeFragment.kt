package com.topiichat.app.features.home

import android.view.LayoutInflater
import android.view.ViewGroup
import com.topiichat.app.core.platform.BaseFragment
import com.topiichat.app.databinding.FragmentHomeBinding

class HomeFragment : BaseFragment<FragmentHomeBinding>(){

    override val tagId: Int
        get() = TODO("Not yet implemented")

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }
}