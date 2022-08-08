package com.topiichat.app.features.chats.chat.presentation.custom

import android.content.Context
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.topiichat.app.R
import com.topiichat.app.core.extension.getDrawableKtx
import com.topiichat.app.databinding.ChatTextMessageViewBinding
import com.topiichat.app.features.chats.chat.domain.model.MessageType
import com.topiichat.app.features.chats.chat.presentation.model.ChatMessageViewModel

internal class ChatTextMessageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = ChatTextMessageViewBinding.inflate(LayoutInflater.from(context), this)

    private var longClickListener: (() -> Unit)? = null

    init {
        binding.textMessage.movementMethod = LinkMovementMethod.getInstance()
        setUpLongClick()
    }

    var message: ChatMessageViewModel? = null
        set(value) {
            if (field != value) {
                field = value
                updateViewState(value)
            }
        }

    private fun setUpLongClick() {
        binding.textMessage.setOnLongClickListener {
            longClickListener?.invoke()
            longClickListener != null
        }
    }

    private fun updateViewState(model: ChatMessageViewModel?) {
        binding.textMessage.text = model?.text
        binding.textTime.text = model?.date
        model?.type?.let { setUpViewStyle(it) }
    }

    private fun setUpViewStyle(type: MessageType) {
        background = when (type) {
            MessageType.Status,
            is MessageType.Incoming -> {
                context.getDrawableKtx(R.drawable.bg_chat_incoming_message)
            }
            is MessageType.Outgoing -> {
                context.getDrawableKtx(R.drawable.bg_chat_outgoing_message)
            }
        }
    }

    fun addOnLongClickListener(onLongClick: () -> Unit) {
        longClickListener = onLongClick
    }
}