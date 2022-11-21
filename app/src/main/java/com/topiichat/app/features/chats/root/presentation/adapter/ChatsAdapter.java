package com.topiichat.app.features.chats.root.presentation.adapter;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.topiichat.app.databinding.ChatListItemBinding;
import com.yourbestigor.chat.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import eu.siacs.conversations.entities.Conversation;
import eu.siacs.conversations.entities.Conversational;
import eu.siacs.conversations.entities.Message;
import eu.siacs.conversations.ui.XmppActivity;
import eu.siacs.conversations.ui.util.AvatarWorkerTask;
import eu.siacs.conversations.utils.IrregularUnicodeDetector;
import eu.siacs.conversations.utils.MimeUtils;
import eu.siacs.conversations.utils.UIHelper;
import eu.siacs.conversations.xmpp.Jid;
import eu.siacs.conversations.xmpp.jingle.OngoingRtpSession;

public class ChatsAdapter
        extends RecyclerView.Adapter<ChatsAdapter.ConversationViewHolder> {

    private final XmppActivity activity;
    private final List<Conversation> conversations;
    private OnConversationClickListener listener;

    public ChatsAdapter(XmppActivity activity, List<Conversation> conversations) {
        this.activity = activity;
        this.conversations = conversations;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversationViewHolder(ChatListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder viewHolder, int position) {
        Conversation conversation = conversations.get(position);
        if (conversation == null) {
            return;
        }
        CharSequence name = conversation.getName();
        if (name instanceof Jid) {
            viewHolder.binding.textMessageRecipient.setText(
                    IrregularUnicodeDetector.style(activity, (Jid) name));
        } else {
            viewHolder.binding.textMessageRecipient.setText(name);
        }

        Message message = conversation.getLatestMessage();
        final int unreadCount = conversation.unreadCount();
        final boolean isRead = conversation.isRead();
        final Conversation.Draft draft = isRead ? conversation.getDraft() : null;
        if (unreadCount > 0) {
            viewHolder.binding.textUnreadMessagesCount.setVisibility(View.VISIBLE);
            viewHolder.binding.textUnreadMessagesCount.setText(String.valueOf(unreadCount));
        } else {
            viewHolder.binding.textUnreadMessagesCount.setVisibility(View.GONE);
        }

        if (isRead) {
            viewHolder.binding.textMessageRecipient.setTypeface(null, Typeface.NORMAL);
        } else {
            viewHolder.binding.textMessageRecipient.setTypeface(null, Typeface.BOLD);
        }

        if (draft != null) {
            viewHolder.binding.imageAvatar.setVisibility(View.GONE);
            viewHolder.binding.textMessageRecipient.setText(activity.getString(R.string.draft) + " " + draft.getMessage());
            viewHolder.binding.textMessageRecipient.setVisibility(View.VISIBLE);
            viewHolder.binding.textMessagePreview.setTypeface(null, Typeface.NORMAL);
            viewHolder.binding.textMessageRecipient.setTypeface(null, Typeface.ITALIC);
        } else {
            final boolean fileAvailable = !message.isDeleted();
            final boolean showPreviewText;
            if (fileAvailable
                    && (message.isFileOrImage()
                    || message.treatAsDownloadable()
                    || message.isGeoUri())) {
                final int imageResource;
                if (message.isGeoUri()) {
                    imageResource =
                            activity.getThemeResource(
                                    R.attr.ic_attach_location, R.drawable.ic_attach_location);
                    showPreviewText = false;
                } else {
                    final String mime = message.getMimeType();
                    if (MimeUtils.AMBIGUOUS_CONTAINER_FORMATS.contains(mime)) {
                        final Message.FileParams fileParams = message.getFileParams();
                        if (fileParams.width > 0 && fileParams.height > 0) {
                            imageResource =
                                    activity.getThemeResource(
                                            R.attr.ic_attach_videocam,
                                            R.drawable.ic_attach_videocam);
                            showPreviewText = false;
                        } else if (fileParams.runtime > 0) {
                            imageResource =
                                    activity.getThemeResource(
                                            R.attr.ic_attach_record, R.drawable.ic_attach_record);
                            showPreviewText = false;
                        } else {
                            imageResource =
                                    activity.getThemeResource(
                                            R.attr.ic_attach_document,
                                            R.drawable.ic_attach_document);
                            showPreviewText = true;
                        }
                    } else {
                        switch (Strings.nullToEmpty(mime).split("/")[0]) {
                            case "image":
                                imageResource =
                                        activity.getThemeResource(
                                                R.attr.ic_attach_photo, R.drawable.ic_attach_photo);
                                showPreviewText = false;
                                break;
                            case "video":
                                imageResource =
                                        activity.getThemeResource(
                                                R.attr.ic_attach_videocam,
                                                R.drawable.ic_attach_videocam);
                                showPreviewText = false;
                                break;
                            case "audio":
                                imageResource =
                                        activity.getThemeResource(
                                                R.attr.ic_attach_record,
                                                R.drawable.ic_attach_record);
                                showPreviewText = false;
                                break;
                            default:
                                imageResource =
                                        activity.getThemeResource(
                                                R.attr.ic_attach_document,
                                                R.drawable.ic_attach_document);
                                showPreviewText = true;
                                break;
                        }
                    }
                }
                viewHolder.binding.imageLastImage.setImageResource(imageResource);
                viewHolder.binding.imageLastImage.setVisibility(View.VISIBLE);
            } else {
                viewHolder.binding.imageLastImage.setVisibility(View.GONE);
                showPreviewText = true;
            }
            final Pair<CharSequence, Boolean> preview =
                    UIHelper.getMessagePreview(
                            activity,
                            message,
                            viewHolder.binding.textMessagePreview.getCurrentTextColor());
            if (showPreviewText) {
                viewHolder.binding.textMessagePreview.setText(UIHelper.shorten(preview.first));
            } else {
                viewHolder.binding.imageLastImage.setContentDescription(preview.first);
            }
            viewHolder.binding.textMessagePreview.setVisibility(
                    showPreviewText ? View.VISIBLE : View.GONE);
            if (preview.second) {
                if (isRead) {
                    viewHolder.binding.textMessagePreview.setTypeface(null, Typeface.ITALIC);
                    viewHolder.binding.textMessageRecipient.setTypeface(null, Typeface.NORMAL);
                } else {
                    viewHolder.binding.textMessagePreview.setTypeface(null, Typeface.BOLD_ITALIC);
                    viewHolder.binding.textMessageRecipient.setTypeface(null, Typeface.BOLD);
                }
            } else {
                if (isRead) {
                    viewHolder.binding.textMessagePreview.setTypeface(null, Typeface.NORMAL);
                    viewHolder.binding.textMessageRecipient.setTypeface(null, Typeface.NORMAL);
                } else {
                    viewHolder.binding.textMessagePreview.setTypeface(null, Typeface.BOLD);
                    viewHolder.binding.textMessageRecipient.setTypeface(null, Typeface.BOLD);
                }
            }

        }

        final Optional<OngoingRtpSession> ongoingCall;
        if (conversation.getMode() == Conversational.MODE_MULTI) {
            ongoingCall = Optional.absent();
        } else {
            ongoingCall =
                    activity.xmppConnectionService
                            .getJingleConnectionManager()
                            .getOngoingRtpConnection(conversation.getContact());
        }

        if (ongoingCall.isPresent()) {
            viewHolder.binding.imageNotificationStatus.setVisibility(View.VISIBLE);
            final int ic_ongoing_call =
                    activity.getThemeResource(
                            R.attr.ic_ongoing_call_hint, R.drawable.ic_phone_in_talk_black_18dp);
            viewHolder.binding.imageNotificationStatus.setImageResource(ic_ongoing_call);
        } else {
            final long muted_till =
                    conversation.getLongAttribute(Conversation.ATTRIBUTE_MUTED_TILL, 0);
            if (muted_till == Long.MAX_VALUE || muted_till >= System.currentTimeMillis()) {
                viewHolder.binding.imageNotificationStatus.setVisibility(View.VISIBLE);
                int ic_notifications_off =
                        activity.getThemeResource(
                                R.attr.icon_notifications_off,
                                R.drawable.ic_notifications_off_black_24dp);
                viewHolder.binding.imageNotificationStatus.setImageResource(ic_notifications_off);
            } else if (conversation.alwaysNotify()) {
                viewHolder.binding.imageNotificationStatus.setVisibility(View.GONE);
            } else {
                viewHolder.binding.imageNotificationStatus.setVisibility(View.VISIBLE);
                int ic_notifications_none =
                        activity.getThemeResource(
                                R.attr.icon_notifications_none,
                                R.drawable.ic_notifications_none_black_24dp);
                viewHolder.binding.imageNotificationStatus.setImageResource(ic_notifications_none);
            }
        }

        long timestamp;
        if (draft != null) {
            timestamp = draft.getTimestamp();
        } else {
            timestamp = conversation.getLatestMessage().getTimeSent();
        }

        viewHolder.binding.textMessageTime.setText(
                UIHelper.readableTimeDifference(activity, timestamp));
        AvatarWorkerTask.loadAvatar(
                conversation,
                viewHolder.binding.imageAvatar,
                R.dimen.avatar_on_conversation_overview);
        viewHolder.itemView.setOnClickListener(v -> listener.onConversationClick(v, conversation));
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    public void setConversationClickListener(OnConversationClickListener listener) {
        this.listener = listener;
    }

    public void insert(Conversation c, int position) {
        conversations.add(position, c);
        notifyDataSetChanged();
    }

    public void remove(Conversation conversation, int position) {
        conversations.remove(conversation);
        notifyItemRemoved(position);
    }

    public interface OnConversationClickListener {
        void onConversationClick(View view, Conversation conversation);
    }

    static class ConversationViewHolder extends RecyclerView.ViewHolder {
        private final ChatListItemBinding binding;

        private ConversationViewHolder(ChatListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
