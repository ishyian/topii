package com.topiichat.app.features.wallet.card.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.topiichat.app.core.constants.InputMasks
import com.topiichat.app.core.extension.setupInputMask
import com.topiichat.app.core.presentation.platform.BaseBottomSheetDialogFragment
import com.topiichat.app.databinding.BottomSheetDialogAddBankCardBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddCardBottomSheetFragment : BaseBottomSheetDialogFragment<BottomSheetDialogAddBankCardBinding>() {
    private val viewModel: AddCardBottomSheetViewModel by viewModels()

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): BottomSheetDialogAddBankCardBinding {
        return BottomSheetDialogAddBankCardBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        setupClickListener(btnAdd)
        initObservers()
        editInputCardNumber.setupInputMask(InputMasks.CARD_NUMBER)
        editInputCardExpiryDate.setupInputMask(InputMasks.EXPIRE_DATE)
    }

    override fun onClick(v: View?) {
        viewModel.onClick(v)
    }

    private fun initObservers() = with(viewModel) {
        observe(dismissDialog) {
            dismiss()
        }
    }
}