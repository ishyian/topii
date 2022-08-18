package com.topiichat.app.features.chats.chat.presentation

import android.net.Uri
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.topiichat.app.R
import com.topiichat.app.core.presentation.platform.BaseViewModel
import com.topiichat.app.core.presentation.platform.SingleLiveData
import com.topiichat.app.features.MainScreens
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

    private val _takePhotoFromCamera: MutableLiveData<String> = MutableLiveData()
    val takePhotoFromCamera: LiveData<String> = _takePhotoFromCamera

    private val _pickImageFromGallery: MutableLiveData<Unit> = MutableLiveData()
    val pickImageFromGallery: LiveData<Unit> = _pickImageFromGallery

    private val _clearInput: MutableLiveData<Unit> = MutableLiveData()
    val clearInput: LiveData<Unit> = _clearInput

    val showAddAttachmentDialog: SingleLiveData<Unit> = SingleLiveData()

    private var input: String = ""

    init {
        _messagesContent.value = messages
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.image_back -> {
                onClickBack()
            }
            R.id.image_send -> {
                onSendClick()
            }
            R.id.image_attach -> {
                onAttachClick()
            }
            R.id.layout_camera -> {
                onTakePictureClick()
            }
            R.id.layout_gallery -> {
                onGalleryClick()
            }
            R.id.layout_contact -> {
                onContactClick()
            }
        }
    }

    override fun onContactClick() {
        navigate(MainScreens.Contacts)
    }

    override fun onGalleryClick() {
        _pickImageFromGallery.postValue(Unit)
    }

    override fun onTakePictureClick() {
        _takePhotoFromCamera.postValue(getNewPhotoFileName())
    }

    override fun onAttachClick() {
        showAddAttachmentDialog.postValue(Unit)
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

    fun onCameraImageSelected(uri: Uri?) {

    }

    fun onGalleryImagePicked(uri: Uri?) {

    }

    override fun getNewPhotoFileName(): String {
        return System.currentTimeMillis().toString() + ".jpg"
    }

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(@Assisted("chatParameters") parameters: ChatParameters): ChatViewModel
    }
}