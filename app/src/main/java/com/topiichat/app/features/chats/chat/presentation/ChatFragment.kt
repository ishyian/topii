package com.topiichat.app.features.chats.chat.presentation

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.topiichat.app.R
import com.topiichat.app.core.contract.PickImageActivityContract
import com.topiichat.app.core.contract.TakePhotoActivityContract
import com.topiichat.app.core.delegates.parcelableParameters
import com.topiichat.app.core.extension.lazyUnsynchronized
import com.topiichat.app.core.extension.viewModelCreator
import com.topiichat.app.core.presentation.platform.BaseFragment
import com.topiichat.app.databinding.DialogAddAttachmentBinding
import com.topiichat.app.databinding.FragmentChatBinding
import com.topiichat.app.features.chats.chat.presentation.adapter.ChatMessagesAdapter
import com.topiichat.app.features.chats.chat.presentation.model.ChatMessageUiModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : BaseFragment<FragmentChatBinding>(), IChatFragment {

    @Inject
    lateinit var factory: ChatViewModel.AssistedFactory
    private val viewModel by viewModelCreator {
        factory.create(parameters)
    }

    private val parameters: ChatParameters by parcelableParameters()

    private val chatMessageAdapter by lazyUnsynchronized {
        ChatMessagesAdapter()
    }

    private val takePhoto = registerForActivityResult(TakePhotoActivityContract()) { uri ->
        viewModel.onCameraImageSelected(uri)
    }

    private val pickPhoto = registerForActivityResult(PickImageActivityContract()) { uri ->
        viewModel.onGalleryImagePicked(uri)
    }

    private val firstItemInsertedObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            onAdapterDataChanged()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            onAdapterDataChanged()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            onAdapterDataChanged()
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            onAdapterDataChanged()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            onAdapterDataChanged()
        }
    }

    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (dy != 0) {
                viewModel.onScrolled()
            }
        }
    }

    private fun onAdapterDataChanged() {
        if (viewModel.shouldScrollToEnd) {
            binding.rvMessagesList.smoothScrollToPosition(chatMessageAdapter.itemCount - 1)
        }
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentChatBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        setupClickListener(imageBack, imageSend, imageAttach, imageRemittance, imageCall, imageVideoCall)
        textRecipientName.text = parameters.from
        editMessageInput.doAfterTextChanged { text -> viewModel.onInputChanged(text.toString()) }
        chatMessageAdapter.registerAdapterDataObserver(firstItemInsertedObserver)
        rvMessagesList.run {
            //addOnScrollListener(onScrollListener)
            //adapter = chatMessageAdapter
        }
        initObservers()
    }

    override fun onVisibilityLoader(isVisibleLoader: Boolean) = Unit

    override fun onClick(v: View?) {
        viewModel.onClick(v)
    }

    private fun initObservers() = with(viewModel) {
        observe(messagesContent, ::onMessagesLoaded)
        observe(showAddAttachmentDialog, ::onShowAddAttachmentDialog)
        observe(clearInput, ::onClearInput)
        observe(viewModel.takePhotoFromCamera, ::onTakePhotoFromCamera)
        observe(viewModel.pickImageFromGallery, ::onPickImageFromGallery)
    }

    override fun onShowAddAttachmentDialog(ignore: Unit) {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        val dialogBinding = DialogAddAttachmentBinding.inflate(layoutInflater, null, false)
        with(dialogBinding) {
            setupClickListener(
                layoutCamera,
                layoutGallery,
                layoutDocument,
                layoutLocation,
                layoutContact,
                layoutTransfer,
                textShareWalletData
            )
            textClose.setOnClickListener { bottomSheetDialog.dismiss() }
            layoutContact.setOnClickListener {
                bottomSheetDialog.dismiss()
                viewModel.onContactClick()
            }
        }
        bottomSheetDialog.setContentView(dialogBinding.root)
        bottomSheetDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        bottomSheetDialog.show()
    }

    private fun onTakePhotoFromCamera(name: String) {
        takePhoto.launch(name)
    }

    private fun onPickImageFromGallery(value: Unit) {
        pickPhoto.launch(value)
    }

    override fun onMessagesLoaded(messages: List<ChatMessageUiModel>) {
        chatMessageAdapter.items = messages
    }

    override fun onClearInput(ignore: Unit) {
        binding.editMessageInput.text?.clear()
    }

    override fun onDestroyView() {
        chatMessageAdapter.unregisterAdapterDataObserver(firstItemInsertedObserver)
        //binding.rvMessagesList.removeOnScrollListener(onScrollListener)
        super.onDestroyView()
    }
}