package com.topiichat.app.features.send_payment.presentation.total_amount

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.topiichat.app.R
import com.topiichat.app.databinding.ViewSendPaymentTotalAmountBinding

class SendPaymentTotalAmountView(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private var _binding: ViewSendPaymentTotalAmountBinding? = null
    private val binding get() = _binding ?: error("home total sum binding is error")

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