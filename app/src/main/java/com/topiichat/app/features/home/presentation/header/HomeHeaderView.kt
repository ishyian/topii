package com.topiichat.app.features.home.presentation.header

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.topiichat.app.R
import com.topiichat.app.core.extension.setTintColor
import com.topiichat.app.databinding.ViewHomeHeaderBinding

class HomeHeaderView(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    private var _binding: ViewHomeHeaderBinding? = null
    private val binding get() = _binding ?: error("home header view binding is error")

    private var options: HomeHeaderViewOptions = HomeHeaderViewOptions()

    private val btnSendPayment get() = binding.layoutSendPayment
    private val btnRequestPayment get() = binding.layoutRequestPayment
    private val btnWallet get() = binding.layoutWallet
    private val btnChat get() = binding.layoutChat

    private var clickListener: HomeHeaderViewClickListener? = null

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
        inflater.inflate(R.layout.view_home_header, this, true)
        _binding = ViewHomeHeaderBinding.bind(this)
        binding.layoutSendPayment.setOnClickListener {
            selectSendPayment(true)
            selectRequestPayment(false)
            clickListener?.onSendPaymentClicked()
        }
        binding.layoutRequestPayment.setOnClickListener {
            selectSendPayment(false)
            selectRequestPayment(true)
            clickListener?.onRequestPaymentClicked()
        }
        binding.layoutChat.setOnClickListener {
            clickListener?.onChatsClicked()
        }
        binding.layoutWallet.setOnClickListener {
            clickListener?.onWalletClicked()
        }
        updateUi()
    }

    fun renderWith(options: HomeHeaderViewOptions) {
        this.options = options
        updateUi()
    }

    fun setupClickListener(clickListener: HomeHeaderViewClickListener) {
        this.clickListener = clickListener
    }

    private fun updateUi() {
        with(options) {
            setSendPaymentEnabled(isSendPaymentEnabled)
            setRequestPaymentEnabled(isRequestPaymentEnabled)
            setWalletEnabled(isWalletEnabled)
            setChatEnabled(isChatEnabled)
        }
    }

    private fun setSendPaymentEnabled(isEnabled: Boolean) {
        if (isEnabled) {
            btnSendPayment.isEnabled = true
            binding.imageSendPayment.setTintColor(R.color.home_header_item_icon_color)
        } else {
            binding.imageSendPayment.setTintColor(R.color.home_header_item_icon_disabled_color)
            btnSendPayment.isEnabled = false
        }
    }

    private fun setRequestPaymentEnabled(isEnabled: Boolean) {
        if (isEnabled) {
            btnRequestPayment.isEnabled = true
            binding.imageRequestPayment.setTintColor(R.color.home_header_item_icon_color)
        } else {
            btnRequestPayment.isEnabled = false
            binding.imageRequestPayment.setTintColor(R.color.home_header_item_icon_disabled_color)
        }
    }

    private fun setWalletEnabled(isEnabled: Boolean) {
        if (isEnabled) {
            btnWallet.isEnabled = true
            binding.imageWallet.setTintColor(R.color.home_header_item_icon_color)
        } else {
            binding.imageWallet.setTintColor(R.color.home_header_item_icon_disabled_color)
            btnWallet.isEnabled = false
        }
    }

    private fun setChatEnabled(isEnabled: Boolean) {
        if (isEnabled) {
            btnChat.isEnabled = true
            binding.imageChat.setTintColor(R.color.home_header_item_icon_color)
        } else {
            binding.imageChat.setTintColor(R.color.home_header_item_icon_disabled_color)
            btnChat.isEnabled = false
        }
    }

    private fun selectSendPayment(isSelected: Boolean) {
        if (isSelected) {
            btnSendPayment.background = ContextCompat.getDrawable(context, R.drawable.bg_home_header_selected_item)
            binding.imageSendPayment.setTintColor(R.color.home_header_item_icon_selected_color)
        } else {
            btnSendPayment.background = ContextCompat.getDrawable(context, R.drawable.bg_home_header_item)
            binding.imageSendPayment.setTintColor(R.color.home_header_item_icon_color)
        }
    }

    private fun selectRequestPayment(isSelected: Boolean) {
        if (isSelected) {
            btnRequestPayment.background = ContextCompat.getDrawable(context, R.drawable.bg_home_header_selected_item)
            binding.imageRequestPayment.setTintColor(R.color.home_header_item_icon_selected_color)
        } else {
            btnRequestPayment.background = ContextCompat.getDrawable(context, R.drawable.bg_home_header_item)
            binding.imageRequestPayment.setTintColor(R.color.home_header_item_icon_color)
        }
    }

    interface HomeHeaderViewClickListener {
        fun onSendPaymentClicked()
        fun onRequestPaymentClicked()
        fun onChatsClicked()
        fun onWalletClicked()
    }
}