package com.topiichat.chat.chat_list.presentation.adapter

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.common.base.Optional
import com.google.common.base.Strings
import com.yourbestigor.chat.R
import com.yourbestigor.chat.databinding.ChatListItemBinding
import eu.siacs.conversations.entities.Conversation
import eu.siacs.conversations.entities.Conversational
import eu.siacs.conversations.ui.XmppActivity
import eu.siacs.conversations.ui.util.AvatarWorkerTask
import eu.siacs.conversations.utils.IrregularUnicodeDetector
import eu.siacs.conversations.utils.MimeUtils
import eu.siacs.conversations.utils.UIHelper
import eu.siacs.conversations.xmpp.Jid
import eu.siacs.conversations.xmpp.jingle.OngoingRtpSession

class ChatsAdapter(
    val activity: XmppActivity,
    private val conversations: MutableList<Conversation>
) : RecyclerView.Adapter<ChatsAdapter.ConversationViewHolder>() {

    private var listener: OnConversationClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        return ConversationViewHolder(ChatListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(viewHolder: ConversationViewHolder, position: Int) {
        val conversation = conversations[position]
        viewHolder.bind(conversation, activity)
        viewHolder.itemView.setOnClickListener { v: View? -> listener?.onConversationClick(v, conversation) }
    }

    override fun getItemCount(): Int {
        return conversations.size
    }

    fun setConversationClickListener(listener: OnConversationClickListener) {
        this.listener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun insert(c: Conversation, position: Int) {
        conversations.add(position, c)
        notifyDataSetChanged()
    }

    fun remove(conversation: Conversation, position: Int) {
        conversations.remove(conversation)
        notifyItemRemoved(position)
    }

    fun interface OnConversationClickListener {
        fun onConversationClick(view: View?, conversation: Conversation?)
    }

    class ConversationViewHolder constructor(val binding: ChatListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(conversation: Conversation, activity: XmppActivity) = with(binding) {
            val name = conversation.name
            if (name is Jid) {
                textMessageRecipient.text = IrregularUnicodeDetector.style(itemView.context, name)
            } else {
                textMessageRecipient.text = name
            }
            val message = conversation.latestMessage
            val unreadCount = conversation.unreadCount()
            val isRead = conversation.isRead
            val draft = if (isRead) conversation.draft else null
            if (unreadCount > 0) {
                textUnreadMessagesCount.visibility = View.VISIBLE
                textUnreadMessagesCount.text = unreadCount.toString()
            } else {
                textUnreadMessagesCount.visibility = View.GONE
            }
            if (isRead) {
                textMessageRecipient.setTypeface(null, Typeface.NORMAL)
            } else {
                textMessageRecipient.setTypeface(null, Typeface.BOLD)
            }
            if (draft != null) {
                imageAvatar.visibility = View.GONE
                textMessageRecipient.text = itemView.context.getString(R.string.draft) + " " + draft.message
                textMessageRecipient.visibility = View.VISIBLE
                textMessagePreview.setTypeface(null, Typeface.NORMAL)
                textMessageRecipient.setTypeface(null, Typeface.ITALIC)
            } else {
                val fileAvailable = !message.isDeleted
                val showPreviewText: Boolean
                if (fileAvailable
                    && (message.isFileOrImage
                        || message.treatAsDownloadable()
                        || message.isGeoUri)
                ) {
                    val imageResource: Int
                    if (message.isGeoUri) {
                        imageResource = R.drawable.ic_attach_location
                        showPreviewText = false
                    } else {
                        val mime = message.mimeType
                        if (MimeUtils.AMBIGUOUS_CONTAINER_FORMATS.contains(mime)) {
                            val fileParams = message.fileParams
                            if (fileParams.width > 0 && fileParams.height > 0) {
                                imageResource = R.drawable.ic_attach_videocam
                                showPreviewText = false
                            } else if (fileParams.runtime > 0) {
                                imageResource = R.drawable.ic_attach_record
                                showPreviewText = false
                            } else {
                                imageResource = R.drawable.ic_attach_document
                                showPreviewText = true
                            }
                        } else {
                            when (Strings.nullToEmpty(mime).split("/").toTypedArray()[0]) {
                                "image" -> {
                                    imageResource = R.drawable.ic_attach_photo
                                    showPreviewText = false
                                }
                                "video" -> {
                                    imageResource = R.drawable.ic_attach_videocam
                                    showPreviewText = false
                                }
                                "audio" -> {
                                    imageResource = R.drawable.ic_attach_record
                                    showPreviewText = false
                                }
                                else -> {
                                    imageResource = R.drawable.ic_attach_document
                                    showPreviewText = true
                                }
                            }
                        }
                    }
                    imageLastImage.setImageResource(imageResource)
                    imageLastImage.visibility = View.VISIBLE
                } else {
                    binding.imageLastImage.visibility = View.GONE
                    showPreviewText = true
                }
                val preview = UIHelper.getMessagePreview(itemView.context, message, textMessagePreview.currentTextColor)
                if (showPreviewText) {
                    textMessagePreview.text = UIHelper.shorten(preview.first)
                } else {
                    imageLastImage.contentDescription = preview.first
                }
                textMessagePreview.visibility = if (showPreviewText) View.VISIBLE else View.GONE
                if (preview.second) {
                    if (isRead) {
                        textMessagePreview.setTypeface(null, Typeface.ITALIC)
                        textMessageRecipient.setTypeface(null, Typeface.NORMAL)
                    } else {
                        textMessagePreview.setTypeface(null, Typeface.BOLD_ITALIC)
                        textMessageRecipient.setTypeface(null, Typeface.BOLD)
                    }
                } else {
                    if (isRead) {
                        textMessagePreview.setTypeface(null, Typeface.NORMAL)
                        textMessageRecipient.setTypeface(null, Typeface.NORMAL)
                    } else {
                        textMessagePreview.setTypeface(null, Typeface.BOLD)
                        textMessageRecipient.setTypeface(null, Typeface.BOLD)
                    }
                }
            }
            val ongoingCall: Optional<OngoingRtpSession> = if (conversation.mode == Conversational.MODE_MULTI) {
                Optional.absent()
            } else {
                activity.xmppConnectionService
                    .jingleConnectionManager
                    .getOngoingRtpConnection(conversation.contact)
            }
            if (ongoingCall.isPresent) {
                imageNotificationStatus.visibility = View.VISIBLE
                imageNotificationStatus.setImageResource(R.drawable.ic_phone_in_talk_black_18dp)
            } else {
                val mutedTill = conversation.getLongAttribute(Conversation.ATTRIBUTE_MUTED_TILL, 0)
                if (mutedTill == Long.MAX_VALUE || mutedTill >= System.currentTimeMillis()) {
                    imageNotificationStatus.visibility = View.VISIBLE
                    imageNotificationStatus.setImageResource(R.drawable.ic_notifications_off_black_24dp)
                } else if (conversation.alwaysNotify()) {
                    imageNotificationStatus.visibility = View.GONE
                } else {
                    imageNotificationStatus.visibility = View.VISIBLE
                    binding.imageNotificationStatus.setImageResource(R.drawable.ic_notifications_none_black_24dp)
                }
            }
            val timestamp: Long = draft?.timestamp ?: conversation.latestMessage.timeSent
            textMessageTime.text = UIHelper.readableTimeDifference(activity, timestamp)
            AvatarWorkerTask.loadAvatar(conversation, binding.imageAvatar, R.dimen.avatar_on_conversation_overview)
        }
    }
}