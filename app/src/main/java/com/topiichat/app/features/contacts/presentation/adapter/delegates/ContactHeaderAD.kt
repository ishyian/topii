package com.topiichat.app.features.contacts.presentation.adapter.delegates

import android.annotation.SuppressLint
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.topiichat.app.databinding.ContactHeaderItemBinding
import com.topiichat.app.features.contacts.presentation.model.ContactLetterUiModel

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