package com.topiichat.app.features.contacts.presentation.adapter.delegates

import android.annotation.SuppressLint
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.topiichat.app.databinding.ContactItemBinding
import com.topiichat.app.features.contacts.presentation.model.ContactUiModel

@SuppressLint("SetTextI18n")
fun contactItemAD(
    onClick: (ContactUiModel) -> Unit
) = adapterDelegateViewBinding<ContactUiModel, Any, ContactItemBinding>(
    { layoutInflater, parent ->
        ContactItemBinding.inflate(layoutInflater, parent, false)
    }
) {
    itemView.setOnClickListener {
        onClick(item)
    }
    bind { payloads ->
        if (payloads.isEmpty()) {
            with(binding) {
                textContactAvatar.text = "${item.displayName.firstOrNull() ?: ""}${item.lastName.firstOrNull() ?: ""}"
                textContactTitle.text = item.displayName
                viewCheck.isActivated = item.isSelected
            }
        } else {
            val pl = payloads.firstOrNull() as List<*>
            val isActivated = pl[0] == true
            binding.viewCheck.isActivated = isActivated
        }
    }
}