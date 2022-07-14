package com.topiichat.app.features.home.presentation.total_sum

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.topiichat.app.R
import com.topiichat.app.databinding.ViewHomeTotalSumByMonthBinding

class HomeTotalSumByMonthView(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private var _binding: ViewHomeTotalSumByMonthBinding? = null
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
        inflater.inflate(R.layout.view_home_total_sum_by_month, this, true)
        _binding = ViewHomeTotalSumByMonthBinding.bind(this)
    }

    @SuppressLint("SetTextI18n")
    fun renderWith(totalSentSum: Double, month: String) {
        binding.textTotalSentSum.text = "$totalSentSum€"
        binding.textMonthName.text = month
    }
}