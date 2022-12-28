package com.topiichat.core.presentation.contacts.presentation.adapter.delegates

import android.annotation.SuppressLint
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.topiichat.app.features.contacts.presentation.model.ContactLetterUiModel
import com.topiichat.core.databinding.ContactHeaderItemBinding

@SuppressLint("SetTextI18n")
fun contactHeaderAD() = adapterDelegateViewBinding<ContactLetterUiModel, Any, ContactHeaderItemBinding>(
    { layoutInflater, parent ->
        ContactHeaderItemBinding.inflate(layoutInflater, parent, false)
    }
) {
    bind {
        binding.textHeader.text = item.letter
    }
}