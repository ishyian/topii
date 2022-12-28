package com.topiichat.chat.chat_contacts.presentation.adapter.delegates

import android.annotation.SuppressLint
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.topiichat.chat.chat_contacts.presentation.model.ChatContactUiModel
import com.yourbestigor.chat.databinding.ChatContactItemBinding
import eu.siacs.conversations.ui.util.AvatarWorkerTask

@SuppressLint("SetTextI18n")
fun chatContactItemAD(
    onClick: (ChatContactUiModel) -> Unit
) = adapterDelegateViewBinding<ChatContactUiModel, Any, ChatContactItemBinding>(
    { layoutInflater, parent ->
        ChatContactItemBinding.inflate(layoutInflater, parent, false)
    }
) {
    itemView.setOnClickListener {
        onClick(item)
    }
    bind {
        binding.textContactTitle.text = item.contact.displayName
        AvatarWorkerTask.loadAvatar(item.contact, binding.textContactAvatar, com.topiichat.core.R.dimen.offset36)
    }
}