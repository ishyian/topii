package com.topiichat.app.features.chats.root.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar.BaseCallback
import com.google.android.material.snackbar.Snackbar
import com.topiichat.app.R
import com.topiichat.app.core.extension.getDrawableKtx
import com.topiichat.app.core.extension.viewModelCreator
import com.topiichat.app.databinding.FragmentChatsBinding
import com.topiichat.app.features.chats.activity.ChatsActivity
import com.topiichat.app.features.chats.base.BaseChatFragment
import com.topiichat.app.features.chats.chat.ChatFragment
import com.topiichat.app.features.chats.chat.ChatFragment.Companion.STATE_SCROLL_POSITION
import com.topiichat.app.features.chats.root.presentation.adapter.ChatsAdapter
import com.topiichat.app.features.chats.root.presentation.adapter.ChatsListAdapter
import dagger.hilt.android.AndroidEntryPoint
import eu.siacs.conversations.entities.Account
import eu.siacs.conversations.entities.Contact
import eu.siacs.conversations.entities.Conversation
import eu.siacs.conversations.entities.Conversational
import eu.siacs.conversations.ui.EnterJidDialog
import eu.siacs.conversations.ui.XmppActivity
import eu.siacs.conversations.ui.interfaces.OnBackendConnected
import eu.siacs.conversations.ui.interfaces.OnConversationArchived
import eu.siacs.conversations.ui.interfaces.OnConversationSelected
import eu.siacs.conversations.ui.util.PendingActionHelper
import eu.siacs.conversations.ui.util.PendingItem
import eu.siacs.conversations.ui.util.ScrollState
import eu.siacs.conversations.utils.AccountUtils
import eu.siacs.conversations.utils.ThemeHelper
import eu.siacs.conversations.xmpp.Jid
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ChatsFragment : BaseChatFragment<FragmentChatsBinding>(), IChatsFragment, OnBackendConnected {

    @Inject
    lateinit var factory: ChatsViewModel.AssistedFactory
    private val viewModel by viewModelCreator { factory.create() }

    private val conversations = arrayListOf<Conversation>()
    private val activatedAccounts = arrayListOf<String>()

    private val swipedConversation = PendingItem<Conversation>()
    private val pendingScrollState = PendingItem<ScrollState>()

    private var swipeEscapeVelocity = 0f
    private val pendingActionHelper = PendingActionHelper()

    private val chatsActionsAdapter by lazy {
        ChatsListAdapter(viewModel::onChatActionClick)
    }

    private val chatsActivity by lazy {
        requireActivity() as ChatsActivity
    }

    private val chatsListAdapter: ChatsAdapter by lazy {
        ChatsAdapter(chatsActivity, conversations)
    }

    private var touchHelper: ItemTouchHelper? = null
    private val callback: ItemTouchHelper.SimpleCallback =
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
                return swipeEscapeVelocity
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                viewHolder.itemView.alpha = 1f
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                pendingActionHelper.execute()
                val position = viewHolder.layoutPosition
                try {
                    swipedConversation.push(conversations[position])
                } catch (e: IndexOutOfBoundsException) {
                    return
                }
                chatsListAdapter.remove(swipedConversation.peek(), position)
                (activity as ChatsActivity).xmppConnectionService.markRead(swipedConversation.peek())
                if (position == 0 && chatsListAdapter.itemCount == 0) {
                    val c = swipedConversation.pop()
                    chatsActivity.xmppConnectionService.archiveConversation(c)
                    return
                }
                val formerlySelected = ChatFragment.getConversation(chatsActivity) === swipedConversation.peek()
                if (activity is OnConversationArchived) {
                    (activity as OnConversationArchived).onConversationArchived(swipedConversation.peek())
                }
                val swiped = swipedConversation.peek()
                val title: Int = if (swiped.mode == Conversational.MODE_MULTI) {
                    if (swiped.mucOptions.isPrivateAndNonAnonymous) {
                        com.yourbestigor.chat.R.string.title_undo_swipe_out_group_chat
                    } else {
                        com.yourbestigor.chat.R.string.title_undo_swipe_out_channel
                    }
                } else {
                    com.yourbestigor.chat.R.string.title_undo_swipe_out_conversation
                }
                val snackbar = Snackbar.make(binding.rvChatsList, title, 5000)
                    .setAction(com.yourbestigor.chat.R.string.undo) {
                        pendingActionHelper.undo()
                        val conversation = swipedConversation.pop()
                        chatsListAdapter.insert(conversation, position)
                        if (formerlySelected) {
                            if (activity is OnConversationSelected) {
                                (activity as OnConversationSelected).onConversationSelected(swiped)
                            }
                        }
                        val layoutManager = binding.rvChatsList.layoutManager as LinearLayoutManager
                        if (position > layoutManager.findLastVisibleItemPosition()) {
                            binding.rvChatsList.smoothScrollToPosition(position)
                        }
                    }
                    .addCallback(object : Snackbar.Callback() {
                        override fun onDismissed(transientBottomBar: Snackbar, event: Int) {
                            when (event) {
                                BaseCallback.DISMISS_EVENT_SWIPE, BaseCallback.DISMISS_EVENT_TIMEOUT -> pendingActionHelper.execute()
                                else -> {
                                    //Ignore
                                }
                            }
                        }
                    })
                pendingActionHelper.push {
                    if (snackbar.isShownOrQueued) {
                        snackbar.dismiss()
                    }
                    val conversation = swipedConversation.pop()
                    if (conversation != null) {
                        if (!conversation.isRead && conversation.mode == Conversation.MODE_SINGLE) {
                            return@push
                        }
                        chatsActivity.xmppConnectionService.archiveConversation(swiped)
                    }
                }
                ThemeHelper.fix(snackbar)
                snackbar.show()
            }
        }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentChatsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        setupClickListener(imageBack, imageRemittance)

        swipeEscapeVelocity = resources.getDimension(com.yourbestigor.chat.R.dimen.swipe_escape_velocity)

        chatsListAdapter.setConversationClickListener(object : ChatsAdapter.OnConversationClickListener {
            override fun onConversationClick(view: View?, conversation: Conversation?) {
                if (activity is OnConversationSelected) {
                    (activity as OnConversationSelected).onConversationSelected(conversation)
                } else {
                    Timber.w("Activity does not implement OnConversationSelected")
                }
            }
        })

        touchHelper = ItemTouchHelper(callback)
        touchHelper?.attachToRecyclerView(rvChatsList)

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
        if (v?.id == R.id.image_back) {
            requireActivity().onBackPressed()
        }
        viewModel.onClick(v)
    }

    override fun onStartChat(ignore: Boolean) {
        val fragmentManager = chatsActivity.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val previousFragment = fragmentManager.findFragmentByTag(XmppActivity.FRAGMENT_TAG_DIALOG)
        if (previousFragment != null) {
            fragmentTransaction.remove(previousFragment)
        }
        fragmentTransaction.addToBackStack(null)
        val dialog = EnterJidDialog.newInstance(
            activatedAccounts,
            getString(com.yourbestigor.chat.R.string.add_contact),
            getString(com.yourbestigor.chat.R.string.add),
            null,
            null,
            true,
            true
        )

        dialog.setOnEnterJidDialogPositiveListener { accountJid: Jid?, contactJid: Jid? ->
            if (!chatsActivity.xmppConnectionServiceBound) {
                return@setOnEnterJidDialogPositiveListener false
            }
            val account: Account = chatsActivity.xmppConnectionService.findAccountByJid(accountJid)
                ?: return@setOnEnterJidDialogPositiveListener true
            val contact = account.roster.getContact(contactJid)
            if (contact.isSelf) {
                switchToConversation(contact)
                return@setOnEnterJidDialogPositiveListener true
            } else if (contact.showInRoster()) {
                throw EnterJidDialog.JidError(getString(com.yourbestigor.chat.R.string.contact_already_exists))
            } else {
                chatsActivity.xmppConnectionService.createContact(contact, true, null)
                switchToConversation(contact)
                return@setOnEnterJidDialogPositiveListener true
            }
        }
        dialog.show(fragmentTransaction, XmppActivity.FRAGMENT_TAG_DIALOG)
    }

    override fun onContentLoaded(content: List<Any>) {
        chatsActionsAdapter.items = content
    }

    override fun onBackendConnected() {
        if (activity != null) {
            activatedAccounts.clear()
            activatedAccounts.addAll(
                AccountUtils.getEnabledAccounts(
                    (requireActivity() as ChatsActivity).xmppConnectionService
                )
            )
        }

        refresh()
    }

    @Suppress("DEPRECATION")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState == null) {
            return
        }
        pendingScrollState.push(savedInstanceState.getParcelable(STATE_SCROLL_POSITION))
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

    private fun switchToConversation(contact: Contact?) {
        val conversation: Conversation =
            chatsActivity.xmppConnectionService.findOrCreateConversation(contact!!.account, contact.jid, false, true)
        val intent = Intent(requireContext(), ChatsActivity::class.java)
        intent.action = ChatsActivity.ACTION_VIEW_CONVERSATION
        intent.putExtra(ChatsActivity.EXTRA_CONVERSATION, conversation.uuid)
        intent.flags = intent.flags or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    private fun setScrollPosition(scrollPosition: ScrollState?) {
        if (scrollPosition != null) {
            val layoutManager = binding.rvChatsList.layoutManager as LinearLayoutManager
            layoutManager.scrollToPositionWithOffset(scrollPosition.position, scrollPosition.offset)
        }
    }

    private fun initObservers() = with(viewModel) {
        observe(content, ::onContentLoaded)
        observe(startChat, ::onStartChat)
    }
}