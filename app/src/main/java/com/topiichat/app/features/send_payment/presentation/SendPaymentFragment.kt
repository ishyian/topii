package com.topiichat.app.features.send_payment.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.topiichat.app.R
import com.topiichat.app.core.presentation.platform.BaseFragment
import com.topiichat.app.databinding.FragmentSendPaymentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SendPaymentFragment : BaseFragment<FragmentSendPaymentBinding>(), ISendPaymentFragment {

    private val viewModel: SendPaymentViewModel by viewModels()

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSendPaymentBinding {
        return FragmentSendPaymentBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) = with(binding) {
        setupClickListener(toolbar.btnBack)
        initObservers()
        cardSendPayment.editInputSum.doAfterTextChanged { text ->
            cardSendPayment.textReceiverSum.text = text.toString()
        }
        cardSendPayment.textReceiverCurrencyPicker.text = getString(R.string.currency_sample)
        cardSendPayment.textSendCurrencyPicker.text = getString(R.string.currency_sample)
    }

    override fun onClick(v: View?) {
        viewModel.onClick(v)
    }

    private fun initObservers() = with(viewModel) {

    }

    override fun onVisibilityLoader(isVisibleLoader: Boolean) = Unit
}