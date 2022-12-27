package com.topiichat.core.presentation.contacts.domain.model

import com.topiichat.app.features.contacts.domain.model.ContactDomain

class ChatContactSelection(
    private var contact: ContactDomain? = null,
    private var listener: OnSelectListener? = null
) {

    fun selectContact(contact: ContactDomain) {
        this.contact = contact
        listener?.onSelected(contact)
    }

    fun setListener(listener: OnSelectListener) {
        this.listener = listener
    }

    fun interface OnSelectListener {
        fun onSelected(contact: ContactDomain)
    }
}