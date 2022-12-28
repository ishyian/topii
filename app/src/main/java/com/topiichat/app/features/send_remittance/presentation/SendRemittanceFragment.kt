package com.topiichat.app.features.send_remittance.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.bumptech.glide.Glide
import com.topiichat.app.R
import com.topiichat.app.databinding.FragmentSendRemittanceBinding
import com.topiichat.app.features.home.domain.model.CountryDomain
import com.topiichat.app.features.send_remittance.domain.model.FxRateDomain
import com.topiichat.app.features.send_remittance.domain.model.RemittancePurposeDomain
import com.topiichat.app.features.send_remittance.presentation.adapter.RecentUsersAdapter
import com.topiichat.app.features.send_remittance.presentation.model.BtnSendEnablingUi
import com.topiichat.app.features.send_remittance.presentation.model.RecentUsersUiModel
import com.topiichat.app.features.send_remittance.presentation.model.RecipientUiModel
import com.topiichat.app.features.send_remittance.presentation.model.SendRemittanceUIModel
import com.topiichat.core.delegates.parcelableParameters
import com.topiichat.core.extension.addStartCircleDrawableFromUrl
import com.topiichat.core.extension.addStartDrawableFromUrl
import com.topiichat.core.extension.lazyUnsynchronized
import com.topiichat.core.extension.showDropDownWhenClick
import com.topiichat.core.extension.showSelectorDialog
import com.topiichat.core.extension.viewModelCreator
import com.topiichat.core.presentation.platform.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SendRemittanceFragment : BaseFragment<FragmentSendRemittanceBinding>(),
    ISendRemittanceFragment,
    CompoundButton.OnCheckedChangeListener,
    AdapterView.OnItemSelectedListener {

    @Inject
    lateinit var factory: SendRemittanceViewModel.AssistedFactory
    private val viewModel by viewModelCreator {
        factory.create(parameters)
    }
    private val parameters: SendRemittanceParameters by parcelableParameters()

    private val recentUsersAdapter by lazyUnsynchronized {
        RecentUsersAdapter(
            onUserClick = viewModel::onRecentUserClick,
            onAddUserClick = viewModel::onAddUserClick
        )
    }

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSendRemittanceBinding {
        return FragmentSendRemittanceBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) = with(binding) {
        rvRecentUsers.run {
            adapter = recentUsersAdapter
        }
        sendPaymentAmount.checkTerms.setOnCheckedChangeListener(this@SendRemittanceFragment)
        editTextRemittanceConcept.onItemSelectedListener = this@SendRemittanceFragment
        editTextNotes.doAfterTextChanged { notesText ->
            notesText?.let { viewModel.onDescriptionChanged(it.toString()) }
        }
        setupClickListener(toolbar.btnBack, sendPaymentAmount.btnSend)
        initObservers()
    }

    override fun initCurrencyPickers(content: SendRemittanceUIModel) = with(content) {
        with(binding) {
            cardSendPayment.editInputSum.setText(currentCountry.limitMin.toString())
            cardSendPayment.editInputSum.doAfterTextChanged { text ->
                if (text != null && text.isNotEmpty()) {
                    viewModel.onAmountChanged(text.toString().toDouble())
                } else viewModel.onAmountChanged(0.0)
            }
            cardSendPayment.textSendCurrencyPicker.addStartDrawableFromUrl(currentCountry.flagImageUrl)
            cardSendPayment.textSendCurrencyPicker.text = currentCountry.currencyCode
            cardSendPayment.textReceiverSum.text = fxRate?.receivingAmount.toString()
            cardSendPayment.textReceiverCurrencyPicker.setOnClickListener {
                showSelectorDialog(
                    getString(R.string.select_receiver_currency),
                    availableCountries.countries.map { it.code }) { _, position ->
                    viewModel.onToCurrencyChanged(availableCountries.countries[position])
                }
            }
        }
    }

    override fun onClick(v: View?) {
        viewModel.onClick(v)
    }

    override fun onReceiverCountryChanged(receiverCountry: CountryDomain) = with(binding) {
        cardSendPayment.textReceiverCurrencyPicker.text = receiverCountry.currencyCode
        cardSendPayment.textReceiverCurrencyPicker.addStartCircleDrawableFromUrl(receiverCountry.flagImageUrl)
    }

    override fun onReceiverSumChanged(amount: Double) = with(binding) {
        cardSendPayment.textReceiverSum.text = amount.toString()
    }

    override fun onSendingSumChanged(amount: Double) = with(binding) {
        cardSendPayment.editInputSum.setText(amount.toString())
    }

    override fun onVisibilityReceiverSumLoader(isVisibleLoader: Boolean) = with(binding) {
        cardSendPayment.textReceiverSum.isVisible = isVisibleLoader.not()
        cardSendPayment.progressReceiverSum.isVisible = isVisibleLoader
    }

    override fun onVisibilityLoader(isVisibleLoader: Boolean) = with(binding) {
        groupContent.isVisible = isVisibleLoader.not()
        progressBar.isVisible = isVisibleLoader
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        viewModel.onCheckedChanged(buttonView?.id, isChecked)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        viewModel.onPurposeSelected(position)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        viewModel.onPurposeNotSelected()
    }

    override fun onEnablingBtnSend(
        btnSendEnablingUi: BtnSendEnablingUi
    ) = with(binding.sendPaymentAmount.btnSend) {
        isEnabled = btnSendEnablingUi.isEnabled
        setBackgroundResource(btnSendEnablingUi.backgroundId)
    }

    override fun onRecentUsersLoaded(recentUsersModel: RecentUsersUiModel) {
        recentUsersAdapter.items = recentUsersModel.items
    }

    override fun onRemittancePurposesLoaded(purposes: List<RemittancePurposeDomain>?) = with(binding) {
        val items = purposes?.map { purpose -> purpose.label } ?: emptyList()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, items)
        editTextRemittanceConcept.apply {
            setText(items.firstOrNull() ?: "")
            setAdapter(adapter)
            threshold = 0
            showDropDownWhenClick()
        }
    }

    override fun onFxRateChanged(fxRate: FxRateDomain) {
        binding.sendPaymentAmount.renderWith(fxRate)
    }

    override fun onZeroTotalAmount(senderCurrency: String) {
        binding.sendPaymentAmount.zeroAmount(senderCurrency)
    }

    override fun onRecipientSelected(recipientUiModel: RecipientUiModel) = with(binding) {
        Glide.with(requireContext())
            .load(recipientUiModel.userData.avatar)
            .circleCrop()
            .into(layoutRecipient.imageAvatar)

        layoutRecipient.textRecipientName.text = recipientUiModel.userData.fullName
        layoutRecipient.textRecipientCountry.text = recipientUiModel.recipientCountry?.name ?: ""
        layoutRecipient.textRecipientCountry.addStartCircleDrawableFromUrl(
            recipientUiModel.recipientCountry?.flagImageUrl ?: ""
        )

        textRecentUsersTitle.isVisible = false
        rvRecentUsers.isVisible = false
        layoutRecipient.root.isVisible = true
    }

    private fun initObservers() = with(viewModel) {
        observe(showMsgError, ::showErrorMessage)
        observe(showLoader, ::onVisibilityLoader)
        observe(content, ::initCurrencyPickers)
        observe(showReceiverSumLoader, ::onVisibilityReceiverSumLoader)
        observe(receiverSum, ::onReceiverSumChanged)
        observe(sendingSum, ::onSendingSumChanged)
        observe(receiverCountry, ::onReceiverCountryChanged)
        observe(remittancePurposes, ::onRemittancePurposesLoaded)
        observe(recentUsers, ::onRecentUsersLoaded)
        observe(btnSendEnabling, ::onEnablingBtnSend)
        observe(fxRate, ::onFxRateChanged)
        observe(zeroTotalAmount, ::onZeroTotalAmount)
        observe(recipientModel, ::onRecipientSelected)
    }
}