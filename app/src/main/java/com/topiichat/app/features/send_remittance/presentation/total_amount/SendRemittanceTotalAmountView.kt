package com.topiichat.app.features.send_remittance.presentation.total_amount

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.topiichat.app.R
import com.topiichat.app.databinding.ViewSendPaymentTotalAmountBinding
import com.topiichat.app.features.send_remittance.domain.model.FxRateDomain

class SendRemittanceTotalAmountView(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    fun renderWith(model: FxRateDomain?) {
        if (model == null) return
        with(binding) {
            textAmountCommission1.text = "${model.fromCurrencyCode} ${model.sendingAmount}"
            textAmountTotal.text = "${model.toCurrencyCode} ${model.receivingAmount}"
        }
    }

    fun zeroAmount(currency: String) {
        with(binding) {
            textAmountCommission1.text = "$currency 0"
            textAmountTotal.text = "$currency 0"
        }
    }

    private var _binding: ViewSendPaymentTotalAmountBinding? = null
    private val binding get() = _binding ?: error("home total sum binding is error")

    val checkTerms get() = binding.switchPayment
    val btnSend get() = binding.btnSend

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : this(context, attrs, defStyleAttr, 0)

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : this(context, attrs, 0)

    constructor(
        context: Context
    ) : this(context, null)

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.view_send_payment_total_amount, this, true)
        _binding = ViewSendPaymentTotalAmountBinding.bind(this)
        binding.root.background = ContextCompat.getDrawable(context, R.drawable.bg_send_payment_total_amount)
    }
}