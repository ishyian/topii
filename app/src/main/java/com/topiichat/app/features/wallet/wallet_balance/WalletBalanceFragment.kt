package com.topiichat.app.features.wallet.wallet_balance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.topiichat.app.R
import com.topiichat.app.core.extension.getColorKtx
import com.topiichat.app.core.extension.getDrawableKtx
import com.topiichat.app.core.extension.viewModelCreator
import com.topiichat.app.core.presentation.platform.BaseFragment
import com.topiichat.app.databinding.FragmentWalletBalanceBinding
import com.topiichat.app.features.home.domain.model.CurrentCountryDomain
import com.topiichat.app.features.wallet.card.presentation.AddCardBottomSheetFragment
import com.topiichat.app.features.wallet.wallet_balance.marker.WalletMarkerView
import com.topiichat.app.features.wallet.wallet_balance.statistics.WalletStatisticsView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WalletBalanceFragment : BaseFragment<FragmentWalletBalanceBinding>(),
    IWalletBalanceFragment,
    WalletStatisticsView.WalletStatisticsViewClickListener {
    @Inject
    lateinit var factory: WalletBalanceViewModel.AssistedFactory
    private val viewModel by viewModelCreator { factory.create() }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentWalletBalanceBinding {
        return FragmentWalletBalanceBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        setupClickListener(btnShowBalance, toolbar.btnBack, imageAddCreditCard)
        initObservers()
        initChart()
    }

    override fun onClick(v: View?) {
        viewModel.onClick(v)
        when (v?.id) {
            R.id.image_add_credit_card -> showAddCardDialog()
        }
    }

    override fun showAddCardDialog() {
        AddCardBottomSheetFragment().show(
            childFragmentManager,
            AddCardBottomSheetFragment::javaClass.name
        )
    }

    override fun onVisibilityLoader(isVisibleLoader: Boolean) = with(binding) {
        groupContent.isVisible = isVisibleLoader.not()
        progressBar.isVisible = isVisibleLoader
    }

    override fun onShowBalanceImage(imageId: Int) = with(binding) {
        btnShowBalance.setImageResource(imageId)
    }

    override fun onShowBalance(balance: String) = with(binding) {
        textBalance.text = balance
    }

    override fun onAvailableFeaturesLoaded(featuresDomain: CurrentCountryDomain) = with(binding) {
        viewStatistics.renderWith(featuresDomain.countryInfo?.currencyCode)
        viewStatistics.setupClickListener(this@WalletBalanceFragment)
    }

    override fun onTransferClicked() {

    }

    override fun onWithdrawClicked() {
        viewModel.navigateToAddBankAccount()
    }

    private fun initObservers() = with(viewModel) {
        observe(showBalanceImage, ::onShowBalanceImage)
        observe(showBalance, ::onShowBalance)
        observe(availableCountryFeatures, ::onAvailableFeaturesLoaded)
        observe(showLoader, ::onVisibilityLoader)
    }

    private fun initChart() = with(binding.walletChart) {
        //TODO Real values
        description.isEnabled = false
        legend.isEnabled = false
        setDrawBorders(false)
        setDrawGridBackground(false)
        setPinchZoom(false)
        setScaleEnabled(false)

        val walletMarkerView = WalletMarkerView(requireContext())
        walletMarkerView.chartView = this
        marker = walletMarkerView

        xAxis.apply {
            setDrawGridLines(false)
            setDrawLabels(false)
            setDrawAxisLine(false)
        }
        axisLeft.apply {
            setDrawGridLines(false)
            setDrawLabels(false)
            setDrawAxisLine(false)
            setDrawBorders(false)
        }
        axisRight.apply {
            setDrawGridLines(false)
            setDrawLabels(false)
            setDrawAxisLine(false)
        }

        val values = ArrayList<Entry>()
        for (i in 0 until 10) {
            val value: Float = (Math.random() * 100).toFloat() - 30
            values.add(Entry(i.toFloat(), value, null))
        }

        val dataSet = LineDataSet(values, "")
        dataSet.setDrawIcons(false)
        dataSet.highLightColor = requireContext().getColorKtx(R.color.colorPrimary)
        dataSet.setDrawCircles(false)
        dataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        dataSet.color = requireContext().getColorKtx(R.color.whiteColor)
        dataSet.setDrawCircleHole(false)
        dataSet.setDrawValues(false)
        dataSet.setDrawFilled(true)
        dataSet.setDrawHorizontalHighlightIndicator(false)
        dataSet.fillFormatter =
            IFillFormatter { _, _ -> axisLeft.axisMinimum }
        val drawable = requireContext().getDrawableKtx(R.drawable.bg_wallet_chart)
        dataSet.fillDrawable = drawable

        val dataSets = ArrayList<ILineDataSet>()
        dataSets.add(dataSet)
        val data = LineData(dataSets)
        setData(data)
    }

}