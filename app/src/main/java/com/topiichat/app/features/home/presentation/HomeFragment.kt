package com.topiichat.app.features.home.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.topiichat.app.core.extension.lazyUnsynchronized
import com.topiichat.app.core.presentation.platform.BaseFragment
import com.topiichat.app.databinding.FragmentHomeBinding
import com.topiichat.app.features.home.presentation.adapter.HomeAdapter
import com.topiichat.app.features.home.presentation.header.HomeHeaderView
import com.topiichat.app.features.home.presentation.header.HomeHeaderViewOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlin.random.Random

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(),
    IHomeFragment,
    HomeHeaderView.HomeHeaderViewClickListener {

    private val viewModel: HomeViewModel by viewModels()

    private val homeAdapter by lazyUnsynchronized {
        HomeAdapter(
            onFiltersClick = viewModel::onFiltersClick,
            onTransactionClick = viewModel::onTransactionClick
        )
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentHomeBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        binding.homeHeader.renderWith(
            HomeHeaderViewOptions(
                isSendPaymentEnabled = true,
                isRequestPaymentEnabled = true,
                isWalletEnabled = true,
                isChatEnabled = true
            )
        )
        binding.homeHeader.setupClickListener(this)
        binding.rvTransactions.run {
            setHasFixedSize(false)
            adapter = homeAdapter
        }
        initTotalSumView()
    }

    private fun initTotalSumView() {
        if (Random.nextBoolean()) {
            binding.homeTotalSum.isVisible = true
            binding.homeTotalSum.renderWith(55.4, 123.5)
        } else {
            binding.homeTotalSumByMonth.isVisible = true
            binding.homeTotalSumByMonth.renderWith(55.4, "January")
        }
    }

    override fun onSendPaymentClicked() {
        viewModel.onSendPaymentClick()
    }

    override fun onRequestPaymentClicked() {
        // TODO: Change UI according to that
    }

    override fun onChatsClicked() {
        viewModel.onChatsClick()
    }

    override fun onWalletClicked() {
        // Nothing for now
    }

    override fun onVisibilityLoader(isVisibleLoader: Boolean) = Unit

    private fun initObservers() = with(viewModel) {
        observe(content, ::onContentLoaded)
    }

    override fun onContentLoaded(content: List<Any>) {
        homeAdapter.items = content
    }
}