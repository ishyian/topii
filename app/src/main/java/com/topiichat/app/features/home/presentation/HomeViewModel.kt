package com.topiichat.app.features.home.presentation

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.topiichat.app.core.presentation.platform.BaseViewModel
import com.topiichat.app.features.home.domain.model.TransactionDomain
import com.topiichat.app.features.home.presentation.model.HomeTransactionUiModel
import com.topiichat.app.features.home.presentation.model.HomeTransactionsHeaderUiModel

class HomeViewModel : BaseViewModel(), IHomeViewModel {

    private val _content: MutableLiveData<List<Any>> = MutableLiveData()
    val content: LiveData<List<Any>> = _content

    init {
        //TODO: Load data from API
        _content.value = listOf(
            HomeTransactionsHeaderUiModel,
            HomeTransactionUiModel(
                TransactionDomain("Joseph Weiss", "05 June  - 09:05", -550.0)
            ),
            HomeTransactionUiModel(
                TransactionDomain("Esther Greenbaum", "03 June - 16:10", 200.0)
            )
        )
    }

    override fun onClick(view: View?) {

    }

    fun onFiltersClick() {

    }

    fun onTransactionClick(transactionDomain: TransactionDomain) {

    }
}