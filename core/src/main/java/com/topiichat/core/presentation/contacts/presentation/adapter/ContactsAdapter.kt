package com.topiichat.app.features.contacts.presentation.adapter

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.topiichat.app.features.contacts.presentation.model.ContactUiModel
import com.topiichat.core.adapter.BaseDiffCallback
import com.topiichat.core.presentation.contacts.presentation.adapter.delegates.contactHeaderAD
import com.topiichat.core.presentation.contacts.presentation.adapter.delegates.contactItemAD

class ContactsAdapter(
    onContactClick: (ContactUiModel) -> Unit
) : AsyncListDifferDelegationAdapter<Any>(DiffCallback) {

    init {
        delegatesManager
            .addDelegate(contactHeaderAD())
            .addDelegate(contactItemAD(onContactClick))
    }

    private companion object DiffCallback : BaseDiffCallback() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any) = when {
            else -> oldItem.javaClass == newItem.javaClass
        }

        override fun getChangePayload(oldItem: Any, newItem: Any): Any? {
            return when {
                oldItem is ContactUiModel && newItem is ContactUiModel -> {
                    return listOf(
                        newItem.isSelected
                    )
                }
                else -> super.getChangePayload(oldItem, newItem)
            }
        }
    }
}