package com.topiichat.app.features.contacts.presentation.adapter.delegates

import android.annotation.SuppressLint
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.topiichat.app.databinding.ContactSelectedItemBinding
import com.topiichat.app.features.contacts.presentation.model.ContactUiModel

@SuppressLint("SetTextI18n")
fun contactSelectedItemAD(
    onRemoveClick: (ContactUiModel) -> Unit
) = adapterDelegateViewBinding<ContactUiModel, Any, ContactSelectedItemBinding>(
    { layoutInflater, parent ->
        ContactSelectedItemBinding.inflate(layoutInflater, parent, false)
    }
) {
    itemView.setOnClickListener {
        onRemoveClick(item)
    }
    bind {
        with(binding) {
            textContactAvatar.text = "${item.displayName.firstOrNull() ?: ""}${item.lastName.firstOrNull() ?: ""}"
            textContactName.text = item.displayName
        }
    }
}