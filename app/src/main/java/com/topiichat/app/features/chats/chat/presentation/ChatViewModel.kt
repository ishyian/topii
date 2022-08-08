package com.topiichat.app.features.chats.chat.presentation

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.topiichat.app.R
import com.topiichat.app.core.presentation.platform.BaseViewModel
import com.topiichat.app.features.chats.chat.domain.model.MessageType
import com.topiichat.app.features.chats.chat.presentation.model.ChatMessageUiModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.terrakok.cicerone.Router

class ChatViewModel @AssistedInject constructor(
    @Assisted("chatParameters") private val parameters: ChatParameters,
    appRouter: Router
) : BaseViewModel(appRouter), IChatViewModel {
    var shouldScrollToEnd = false

    private var messages = arrayListOf(
        ChatMessageUiModel.Text(
            id = "0",
            type = MessageType.Incoming(name = "Alberto García", avatar = null),
            date = "10:22",
            text = "Hola! Esto es un mensaje"
        ),
        ChatMessageUiModel.Text(
            id = "1",
            type = MessageType.Outgoing,
            date = "10:24",
            text = "Este mensaje ocupa dos líneas y está alineado a la derecha"
        )
    )

    private val _messagesContent: MutableLiveData<List<ChatMessageUiModel>> = MutableLiveData()
    val messagesContent: LiveData<List<ChatMessageUiModel>> = _messagesContent

    private val _clearInput: MutableLiveData<Unit> = MutableLiveData()
    val clearInput: LiveData<Unit> = _clearInput

    private var input: String = ""

    init {
        _messagesContent.value = messages
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.image_send -> {
                onSendClick()
            }
        }
    }

    override fun onInputChanged(input: String) {
        this.input = input
    }

    override fun onSendClick() {
        if (input.isEmpty()) return
        messages = arrayListOf<ChatMessageUiModel.Text>().apply {
            addAll(messages)
            add(
                ChatMessageUiModel.Text(
                    id = "3",
                    type = MessageType.Outgoing,
                    date = "10:24",
                    text = input
                )
            )
        }
        shouldScrollToEnd = true
        _messagesContent.postValue(messages)
        _clearInput.postValue(Unit)
    }

    override fun onScrolled() {
        shouldScrollToEnd = false
    }

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(@Assisted("chatParameters") parameters: ChatParameters): ChatViewModel
    }
}