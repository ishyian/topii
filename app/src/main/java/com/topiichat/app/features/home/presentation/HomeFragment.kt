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
import com.topiichat.app.features.home.domain.model.CountryCode
import com.topiichat.app.features.home.domain.model.CurrentCountryDomain
import com.topiichat.app.features.home.presentation.adapter.HomeAdapter
import com.topiichat.app.features.home.presentation.header.HomeHeaderView
import com.topiichat.app.features.home.presentation.model.HomeRemittanceHistoryUiModel
import com.topiichat.app.features.home.presentation.model.HomeTransactionsHeaderUiModel
import com.topiichat.app.features.home.presentation.total_sum.HomeTotalSumByMonthView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(),
    IHomeFragment,
    HomeHeaderView.HomeHeaderViewClickListener,
    HomeTotalSumByMonthView.OnMonthChangedListener {

    private val viewModel: HomeViewModel by viewModels()

    private val homeAdapter by lazyUnsynchronized {
        HomeAdapter(
            onFiltersClick = viewModel::onFiltersClick,
            onTransactionClick = viewModel::onTransactionClick
        )
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentHomeBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        rvTransactions.run {
            setHasFixedSize(false)
            adapter = homeAdapter
        }
        binding.homeTotalSumByMonth.setupMonthChangedListener(this@HomeFragment)
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

    override fun onVisibilityLoader(isVisibleLoader: Boolean) = with(binding) {
        groupContent.isVisible = isVisibleLoader.not()
        progressBar.isVisible = isVisibleLoader
    }

    private fun initObservers() = with(viewModel) {
        observe(content, ::onContentLoaded)
        observe(availableCountryFeatures, ::onAvailableFeaturesLoaded)
        observe(showMsgError, ::onShowMessageError)
        observe(showLoader, ::onVisibilityLoader)
        observe(showTotalSumByMonthLoader, ::onTotalSumByMonthLoader)
    }

    private fun onTotalSumByMonthLoader(isLoading: Boolean) {
        binding.homeTotalSumByMonth.isLoading = isLoading
    }

    private fun onAvailableFeaturesLoaded(featuresDomain: CurrentCountryDomain) = with(binding) {
        homeHeader.renderWith(featuresDomain)
        homeHeader.setupClickListener(this@HomeFragment)
        when (featuresDomain.countryInfo?.countryCode) {
            CountryCode.US -> {
                homeTotalSumByMonth.isVisible = true
            }
            CountryCode.GT -> {
                homeTotalSum.isVisible = true
            }
            else -> {
                // Nothing
            }
        }
    }

    override fun onShowMessageError(message: String) {
        showToast(message)
    }

    override fun onMonthChanged(month: Int) {
        viewModel.loadRemmittanceHistory(month)
    }

    override fun onContentLoaded(content: HomeRemittanceHistoryUiModel) {
        binding.homeTotalSumByMonth.renderWith(content.totalSent, content.currency)
        binding.homeTotalSum.renderWith(content.totalSent, content.totalReceived, content.currency)
        homeAdapter.items = listOf(HomeTransactionsHeaderUiModel).plus(content.remittances)
    }
}