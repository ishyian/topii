package com.topiichat.chat.search.presentation.adapter

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.yourbestigor.chat.R
import com.yourbestigor.chat.databinding.ChatListItemBinding
import eu.siacs.conversations.entities.Message
import eu.siacs.conversations.ui.XmppActivity
import eu.siacs.conversations.ui.util.AvatarWorkerTask
import eu.siacs.conversations.utils.UIHelper

class SearchMessagesAdapter(
    val activity: XmppActivity,
    private val messages: MutableList<Message>
) : RecyclerView.Adapter<SearchMessagesAdapter.ConversationViewHolder>() {

    private var listener: OnMessageClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        return ConversationViewHolder(ChatListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(viewHolder: ConversationViewHolder, position: Int) {
        val message = messages[position]
        viewHolder.bind(message, activity)
        viewHolder.itemView.setOnClickListener { v: View? -> listener?.onMessageClick(v, message) }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    fun setConversationClickListener(listener: OnMessageClickListener) {
        this.listener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun insert(c: Message, position: Int) {
        messages.add(position, c)
        notifyDataSetChanged()
    }

    fun remove(message: Message, position: Int) {
        messages.remove(message)
        notifyItemRemoved(position)
    }

    interface OnMessageClickListener {
        fun onMessageClick(view: View?, message: Message?)
    }

    class ConversationViewHolder constructor(val binding: ChatListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(message: Message, activity: XmppActivity) = with(binding) {
            val name = message.avatarName
            textMessageRecipient.text = name
            val messageText = message.body
            val isRead = message.isRead
            if (isRead) {
                textMessageRecipient.setTypeface(null, Typeface.NORMAL)
            } else {
                textMessageRecipient.setTypeface(null, Typeface.BOLD)
            }
            val timestamp: Long = message.timeSent
            textMessageTime.text = UIHelper.readableTimeDifference(activity, timestamp)
            textMessagePreview.text = messageText
            imageLastImage.isVisible = false
            AvatarWorkerTask.loadAvatar(message, binding.imageAvatar, R.dimen.avatar_on_conversation_overview)
        }
    }
}