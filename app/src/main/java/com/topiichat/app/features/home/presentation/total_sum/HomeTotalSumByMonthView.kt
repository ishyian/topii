package com.topiichat.app.features.home.presentation.total_sum

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.topiichat.app.R
import com.topiichat.app.databinding.ViewHomeTotalSumByMonthBinding
import org.threeten.bp.LocalDateTime

class HomeTotalSumByMonthView(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val currentMonthValue = LocalDateTime.now().monthValue
    private var month = LocalDateTime.now().month
    private var monthValue = month.value
    private val year = LocalDateTime.now().year

    private var _binding: ViewHomeTotalSumByMonthBinding? = null
    private val binding get() = _binding ?: error("home total sum binding is error")

    var isLoading: Boolean = false
        set(value) {
            field = value
            binding.textTotalSentSum.isInvisible = value
            binding.textTotalSentSumTitle.isInvisible = value
            binding.progressTotalSum.isInvisible = value.not()
            binding.imageArrowRight.isEnabled = value.not()
            binding.imageArrowLeft.isEnabled = value.not()
        }

    private var changedListener: OnMonthChangedListener? = null

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
        month = LocalDateTime.now().month
        updateArrowsUi(monthValue)
        binding.textMonthName.text = month.name
        binding.imageArrowLeft.setOnClickListener {
            if (monthValue > 1) {
                monthValue--
                updateArrowsUi(monthValue)
                month = LocalDateTime.of(year, monthValue, 1, 1, 1).month
                binding.textMonthName.text = month.name
                changedListener?.onMonthChanged(monthValue)
            }
        }
        binding.imageArrowRight.setOnClickListener {
            if (monthValue < 12 && monthValue < currentMonthValue) {
                monthValue++
                updateArrowsUi(monthValue)
                month = LocalDateTime.of(year, monthValue, 1, 1, 1).month
                binding.textMonthName.text = month.name
                changedListener?.onMonthChanged(monthValue)
            }
        }
    }

    private fun updateArrowsUi(month: Int) {
        binding.imageArrowRight.isVisible = month < 12 && month < currentMonthValue
        binding.imageArrowLeft.isVisible = month > 1
    }

    @SuppressLint("SetTextI18n")
    fun renderWith(totalSentSum: Long, currency: String) {
        binding.textTotalSentSum.text = "$currency $totalSentSum"
    }

    fun setupMonthChangedListener(listener: OnMonthChangedListener) {
        this.changedListener = listener
    }

    interface OnMonthChangedListener {
        fun onMonthChanged(month: Int)
    }
}