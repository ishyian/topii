package com.topiichat.app.features.wallet.wallet_balance.statistics

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.topiichat.app.R
import com.topiichat.app.databinding.ViewWalletStatisticsBinding

class WalletStatisticsView(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private var _binding: ViewWalletStatisticsBinding? = null
    private val binding get() = _binding ?: error("wallet statistics view binding is error")

    private val btnTransfer get() = binding.layoutTransfer
    private val btnWithdraw get() = binding.layoutWithdraw

    private var clickListener: WalletStatisticsViewClickListener? = null

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
        inflater.inflate(R.layout.view_wallet_statistics, this, true)
        _binding = ViewWalletStatisticsBinding.bind(this)
        btnTransfer.setOnClickListener {
            clickListener?.onTransferClicked()
        }
        btnWithdraw.setOnClickListener {
            clickListener?.onWithdrawClicked()
        }
    }

    fun renderWith(currencyCode: String?) = with(binding) {
        textIncomeAmount.text = "5445 $currencyCode" //TODO Real amount
        textExpenseAmount.text = "8002 $currencyCode" //TODO Real amount
    }

    fun setupClickListener(clickListener: WalletStatisticsViewClickListener) {
        this.clickListener = clickListener
    }

    interface WalletStatisticsViewClickListener {
        fun onTransferClicked()
        fun onWithdrawClicked()
    }
}