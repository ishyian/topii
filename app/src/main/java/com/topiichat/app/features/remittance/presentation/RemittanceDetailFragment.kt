package com.topiichat.app.features.remittance.presentation

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.bold
import androidx.core.text.color
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.topiichat.app.R
import com.topiichat.app.core.delegates.parcelableParameters
import com.topiichat.app.core.extension.addStartCircleDrawableFromUrl
import com.topiichat.app.core.extension.date.DateFormats
import com.topiichat.app.core.extension.date.toString
import com.topiichat.app.core.extension.getColorKtx
import com.topiichat.app.core.extension.viewModelCreator
import com.topiichat.app.core.presentation.platform.BaseFragment
import com.topiichat.app.databinding.FragmentRemittanceDetailBinding
import com.topiichat.app.features.send_remittance.domain.model.RemittanceDomain
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RemittanceDetailFragment : BaseFragment<FragmentRemittanceDetailBinding>(), IRemittanceDetailFragment {

    @Inject
    lateinit var factory: RemittanceDetailViewModel.AssistedFactory
    private val viewModel by viewModelCreator {
        factory.create(parameters)
    }
    private val parameters: RemittanceParameters by parcelableParameters()

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRemittanceDetailBinding {
        return FragmentRemittanceDetailBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        setupClickListener(toolbar.btnBack)
        initObservers()
    }

    override fun onClick(v: View?) {
        viewModel.onClick(v)
    }

    private fun initObservers() = with(viewModel) {
        observe(showLoader, ::onVisibilityLoader)
        observe(remittanceDetail, ::onRemittanceDetailLoaded)
    }

    override fun onRemittanceDetailLoaded(remittance: RemittanceDomain) = with(binding) {
        val sendingText = remittance.sending.sendingText()
        val receivingText = remittance.receiving.receivingText()
        val exchangeRateText = remittance.exchangeRateText()
        val feeText = remittance.fee.feeText()
        val idText = remittance.maskedIdText()
        val recipientName = remittance.recipient.profile.fullName()

        with(layoutRecipient) {
            Glide.with(requireContext())
                .load(remittance.recipient.profile.avatar)
                .circleCrop()
                .into(layoutRecipient.imageAvatar)
            textRecipientName.text = recipientName
            textRecipientCountry.text = parameters.toCountryName
            textRecipientCountry.addStartCircleDrawableFromUrl(parameters.countryFlag)
        }

        textRemittanceDate.text = remittance.createdAt.toString(DateFormats.TRANSACTION_ITEM_FORMAT)
        textRemittanceId.text = idText
        textRemittanceTime.text = SpannableStringBuilder()
            .append(getString(R.string.remittance_time_1))
            .bold {
                color(requireContext().getColorKtx(R.color.remittance_detail_secondary_text_color)) {
                    append(" ${parameters.remittanceSendRequestTimeSeconds} ${getString(R.string.remittance_time_2)} ")
                }
            }
            .append(getString(R.string.remittance_time_3))

        textAmount.text = sendingText
        textRemittanceSummary.text = SpannableStringBuilder()
            .append(getString(R.string.remittance_detail_summary_1))
            .bold { append(" $sendingText ") }
            .append(getString(R.string.remittance_detail_summary_2))
            .bold { append(" $receivingText ") }
            .append(getString(R.string.remittance_detail_summary_3))
            .bold { append(" $exchangeRateText ") }
            .append(getString(R.string.remittance_detail_summary_4))
            .bold { append(" $feeText ") }
            .append(getString(R.string.remittance_detail_summary_5))
            .bold { append(" $recipientName") }
    }

    override fun onVisibilityLoader(
        isVisibleLoader: Boolean
    ) = with(binding) {
        toolbar.isVisible = isVisibleLoader.not()
        layoutContent.isVisible = isVisibleLoader.not()
        progressBar.isVisible = isVisibleLoader
    }
}