package com.topiichat.app.features.contacts.presentation.adapter.delegates

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.topiichat.app.features.contacts.presentation.model.ContactUiModel
import com.topiichat.core.adapter.BaseDiffCallback

class ContactsSelectedAdapter(
    onRemoveContactClick: (ContactUiModel) -> Unit
) : AsyncListDifferDelegationAdapter<Any>(DiffCallback) {

    init {
        delegatesManager
            .addDelegate(contactSelectedItemAD(onRemoveContactClick))
    }

    private companion object DiffCallback : BaseDiffCallback() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any) = when {
            else -> oldItem.javaClass == newItem.javaClass
        }
    }
}