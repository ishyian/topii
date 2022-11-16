package com.topiichat.app.features.chats.root.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.topiichat.app.R
import com.topiichat.app.core.extension.getDrawableKtx
import com.topiichat.app.core.extension.viewModelCreator
import com.topiichat.app.databinding.FragmentChatsBinding
import com.topiichat.app.features.chats.activity.ChatsActivity
import com.topiichat.app.features.chats.base.BaseChatFragment
import com.topiichat.app.features.chats.root.presentation.adapter.ChatsAdapter
import com.topiichat.app.features.chats.root.presentation.adapter.ChatsListAdapter
import dagger.hilt.android.AndroidEntryPoint
import eu.siacs.conversations.entities.Conversation
import eu.siacs.conversations.ui.ConversationsOverviewFragment
import eu.siacs.conversations.ui.StartConversationActivity
import eu.siacs.conversations.ui.interfaces.OnBackendConnected
import eu.siacs.conversations.ui.interfaces.OnConversationSelected
import eu.siacs.conversations.ui.util.PendingActionHelper
import eu.siacs.conversations.ui.util.PendingItem
import eu.siacs.conversations.ui.util.ScrollState
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ChatsFragment : BaseChatFragment<FragmentChatsBinding>(), IChatsFragment, OnBackendConnected {

    @Inject
    lateinit var factory: ChatsViewModel.AssistedFactory
    private val viewModel by viewModelCreator { factory.create() }

    private val conversations: ArrayList<Conversation> = ArrayList()
    private val swipedConversation = PendingItem<Conversation>()
    private val pendingScrollState = PendingItem<ScrollState>()

    private var mSwipeEscapeVelocity = 0f
    private val pendingActionHelper = PendingActionHelper()

    private val chatsActionsAdapter by lazy {
        ChatsListAdapter(viewModel::onChatActionClick)
    }
    private lateinit var chatsListAdapter: ChatsAdapter

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentChatsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        setupClickListener(imageBack, imageRemittance)

        mSwipeEscapeVelocity = resources.getDimension(com.yourbestigor.chat.R.dimen.swipe_escape_velocity)

        chatsListAdapter = ChatsAdapter(requireActivity() as ChatsActivity, conversations)
        chatsListAdapter.setConversationClickListener { view: View?, conversation: Conversation? ->
            if (activity is OnConversationSelected) {
                (activity as OnConversationSelected).onConversationSelected(conversation)
            } else {
                Timber.w("Activity does not implement OnConversationSelected")
            }
        }
        //this.touchHelper = ItemTouchHelper(this.callback)
        //this.touchHelper.attachToRecyclerView(binding.list)
        val dividerDrawable = requireContext().getDrawableKtx(R.drawable.chats_list_divider)
        val itemDecoration = DividerItemDecoration(
            requireContext(),
            DividerItemDecoration.VERTICAL
        )
        dividerDrawable?.let {
            itemDecoration.setDrawable(it)
        }

        rvChatsActions.run {
            addItemDecoration(itemDecoration)
            adapter = chatsActionsAdapter
        }

        rvChatsList.run {
            addItemDecoration(itemDecoration)
            adapter = chatsListAdapter
        }
        initObservers()
    }

    override fun onVisibilityLoader(isVisibleLoader: Boolean) = Unit

    override fun onClick(v: View?) {
        viewModel.onClick(v)
    }

    private fun initObservers() = with(viewModel) {
        observe(content, ::onContentLoaded)
        observe(startChat, ::onStartChat)
    }

    override fun onStartChat(ignore: Boolean) {
        startActivity(Intent(requireContext(), StartConversationActivity::class.java))
    }

    override fun onContentLoaded(content: List<Any>) {
        chatsActionsAdapter.items = content
    }

    override fun onBackendConnected() {
        refresh()
    }

    @Suppress("DEPRECATION")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState == null) {
            return
        }
        pendingScrollState.push(savedInstanceState.getParcelable(ConversationsOverviewFragment.STATE_SCROLL_POSITION))
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun refresh() {
        if (activity == null) {
            Timber.d("ChatsFragment.refresh() skipped updated because activity was null")
            return
        }

        (activity as ChatsActivity).xmppConnectionService.populateWithOrderedConversations(conversations)
        val removed: Conversation? = swipedConversation.peek()
        if (removed != null) {
            if (removed.isRead) {
                conversations.remove(removed)
            } else {
                pendingActionHelper.execute()
            }
        }
        chatsListAdapter.notifyDataSetChanged()
        val scrollState: ScrollState? = pendingScrollState.pop()
        if (scrollState != null) {
            setScrollPosition(scrollState)
        }
    }

    private fun setScrollPosition(scrollPosition: ScrollState?) {
        if (scrollPosition != null) {
            val layoutManager = binding.rvChatsList.layoutManager as LinearLayoutManager
            layoutManager.scrollToPositionWithOffset(scrollPosition.position, scrollPosition.offset)
        }
    }
}