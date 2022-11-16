package com.topiichat.app.features.chats.new_chat.adapter

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.preference.PreferenceManager
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.format.DateUtils
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.common.base.Strings
import com.yourbestigor.chat.R
import eu.siacs.conversations.Config
import eu.siacs.conversations.entities.Conversation
import eu.siacs.conversations.entities.Message
import eu.siacs.conversations.entities.Message.MergeSeparator
import eu.siacs.conversations.entities.RtpSessionStatus
import eu.siacs.conversations.entities.Transferable
import eu.siacs.conversations.persistance.FileBackend
import eu.siacs.conversations.services.NotificationService
import eu.siacs.conversations.ui.ConversationFragment
import eu.siacs.conversations.ui.ConversationsActivity
import eu.siacs.conversations.ui.XmppActivity
import eu.siacs.conversations.ui.text.DividerSpan
import eu.siacs.conversations.ui.text.QuoteSpan
import eu.siacs.conversations.ui.util.AvatarWorkerTask
import eu.siacs.conversations.ui.util.MyLinkify
import eu.siacs.conversations.ui.util.QuoteHelper
import eu.siacs.conversations.ui.util.ViewUtil
import eu.siacs.conversations.ui.widget.ClickableMovementMethod
import eu.siacs.conversations.utils.CryptoHelper
import eu.siacs.conversations.utils.Emoticons
import eu.siacs.conversations.utils.GeoHelper
import eu.siacs.conversations.utils.MessageUtils
import eu.siacs.conversations.utils.StylingHelper
import eu.siacs.conversations.utils.TimeFrameUtils
import eu.siacs.conversations.utils.UIHelper
import eu.siacs.conversations.xmpp.mam.MamReference
import java.net.URI

class MessageAdapter @JvmOverloads constructor(
    activity: XmppActivity,
    messages: List<Message?>?,
    forceNames: Boolean = false
) : ArrayAdapter<Message?>(activity, 0, messages!!) {
    private val activity: XmppActivity
    private val audioPlayer: AudioPlayer = AudioPlayer(this)
    private var highlightedTerm: List<String>? = null
    private val metrics: DisplayMetrics
    private var mOnContactPictureClickedListener: OnContactPictureClicked? = null
    private var mOnContactPictureLongClickedListener: OnContactPictureLongClicked? = null
    private var mUseGreenBackground = false
    private val mForceNames: Boolean
    fun flagScreenOn() {
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    fun flagScreenOff() {
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    fun setVolumeControl(stream: Int) {
        activity.volumeControlStream = stream
    }

    fun setOnContactPictureClicked(listener: OnContactPictureClicked?) {
        mOnContactPictureClickedListener = listener
    }

    fun getActivity(): Activity {
        return activity
    }

    fun setOnContactPictureLongClicked(
        listener: OnContactPictureLongClicked?
    ) {
        mOnContactPictureLongClickedListener = listener
    }

    override fun getViewTypeCount(): Int {
        return 5
    }

    private fun getItemViewType(message: Message?): Int {
        return if (message!!.type == Message.TYPE_STATUS) {
            if (DATE_SEPARATOR_BODY == message.body) {
                DATE_SEPARATOR
            } else {
                STATUS
            }
        } else if (message.type == Message.TYPE_RTP_SESSION) {
            RTP_SESSION
        } else if (message.status <= Message.STATUS_RECEIVED) {
            RECEIVED
        } else {
            SENT
        }
    }

    override fun getItemViewType(position: Int): Int {
        return this.getItemViewType(getItem(position))
    }

    private fun getMessageTextColor(onDark: Boolean, primary: Boolean): Int {
        return ContextCompat.getColor(activity, com.topiichat.app.R.color.chat_text_message_color)
    }

    private fun displayStatus(viewHolder: ViewHolder, message: Message, type: Int, darkBackground: Boolean) {
        var filesize: String? = null
        var info: String? = null
        var error = false
        if (viewHolder.indicatorReceived != null) {
            viewHolder.indicatorReceived!!.visibility = View.GONE
        }
        if (viewHolder.edit_indicator != null) {
            if (message.edited()) {
                viewHolder.edit_indicator!!.visibility = View.VISIBLE
                viewHolder.edit_indicator!!.setImageResource(if (darkBackground) R.drawable.ic_mode_edit_white_18dp else R.drawable.ic_mode_edit_black_18dp)
                viewHolder.edit_indicator!!.alpha = if (darkBackground) 0.7f else 0.57f
            } else {
                viewHolder.edit_indicator!!.visibility = View.GONE
            }
        }
        val transferable = message.transferable
        val multiReceived = (message.conversation.mode == Conversation.MODE_MULTI
            && message.mergedStatus <= Message.STATUS_RECEIVED)
        if (message.isFileOrImage || transferable != null || MessageUtils.unInitiatedButKnownSize(message)) {
            val params = message.fileParams
            filesize = if (params.size != null) UIHelper.filesizeToString(params.size) else null
            if (transferable != null && (transferable.status == Transferable.STATUS_FAILED || transferable.status == Transferable.STATUS_CANCELLED)) {
                error = true
            }
        }
        when (message.mergedStatus) {
            Message.STATUS_WAITING -> info = context.getString(R.string.waiting)
            Message.STATUS_UNSEND -> info = if (transferable != null) {
                context.getString(R.string.sending_file, transferable.progress)
            } else {
                context.getString(R.string.sending)
            }
            Message.STATUS_OFFERED -> info = context.getString(R.string.offering)
            Message.STATUS_SEND_RECEIVED, Message.STATUS_SEND_DISPLAYED -> {
                viewHolder.indicatorReceived!!.setImageResource(if (darkBackground) R.drawable.ic_done_white_18dp else R.drawable.ic_done_black_18dp)
                viewHolder.indicatorReceived!!.alpha = if (darkBackground) 0.7f else 0.57f
                viewHolder.indicatorReceived!!.visibility = View.VISIBLE
            }
            Message.STATUS_SEND_FAILED -> {
                val errorMessage = message.errorMessage
                info = if (Message.ERROR_MESSAGE_CANCELLED == errorMessage) {
                    context.getString(R.string.cancelled)
                } else if (errorMessage != null) {
                    val errorParts = errorMessage.split("\\u001f".toRegex(), 2).toTypedArray()
                    if (errorParts.size == 2) {
                        when (errorParts[0]) {
                            "file-too-large" -> context.getString(R.string.file_too_large)
                            else -> context.getString(R.string.send_failed)
                        }
                    } else {
                        context.getString(R.string.send_failed)
                    }
                } else {
                    context.getString(R.string.send_failed)
                }
                error = true
            }
            else -> if (mForceNames || multiReceived) {
                info = UIHelper.getMessageDisplayName(message)
            }
        }
        if (error && type == SENT) {
            if (darkBackground) {
                viewHolder.time!!.setTextAppearance(
                    context,
                    R.style.TextAppearance_Conversations_Caption_Warning_OnDark
                )
            } else {
                viewHolder.time!!.setTextAppearance(context, R.style.TextAppearance_Conversations_Caption_Warning)
            }
        } else {
            if (darkBackground) {
                viewHolder.time?.setTextAppearance(context, R.style.TextAppearance_Conversations_Caption_OnDark)
            } else {
                viewHolder.time?.setTextAppearance(context, R.style.TextAppearance_Conversations_Caption)
            }
            viewHolder.time?.setTextColor(getMessageTextColor(darkBackground, false))
        }
        if (message.encryption == Message.ENCRYPTION_NONE) {
            viewHolder.indicator!!.visibility = View.GONE
        } else {
            var verified = false
            if (message.encryption == Message.ENCRYPTION_AXOLOTL) {
                val status = message.conversation
                    .account.axolotlService.getFingerprintTrust(
                        message.fingerprint
                    )
                if (status != null && status.isVerified) {
                    verified = true
                }
            }
            if (verified) {
                viewHolder.indicator!!.setImageResource(if (darkBackground) R.drawable.ic_verified_user_white_18dp else R.drawable.ic_verified_user_black_18dp)
            } else {
                viewHolder.indicator!!.setImageResource(if (darkBackground) R.drawable.ic_lock_white_18dp else R.drawable.ic_lock_black_18dp)
            }
            if (darkBackground) {
                viewHolder.indicator!!.alpha = 0.7f
            } else {
                viewHolder.indicator!!.alpha = 0.57f
            }
            viewHolder.indicator!!.visibility = View.VISIBLE
        }
        val formattedTime = UIHelper.readableTimeDifferenceFull(context, message.mergedTimeSent)
        val bodyLanguage = message.bodyLanguage
        val bodyLanguageInfo = if (bodyLanguage == null) "" else String.format(" \u00B7 %s", bodyLanguage.uppercase())
        if (message.status <= Message.STATUS_RECEIVED) {
            if (filesize != null && info != null) {
                viewHolder.time!!.text = "$formattedTime \u00B7 $filesize \u00B7 $info$bodyLanguageInfo"
            } else if (filesize == null && info != null) {
                viewHolder.time!!.text = "$formattedTime \u00B7 $info$bodyLanguageInfo"
            } else if (filesize != null && info == null) {
                viewHolder.time!!.text = "$formattedTime \u00B7 $filesize$bodyLanguageInfo"
            } else {
                viewHolder.time!!.text = formattedTime + bodyLanguageInfo
            }
        } else {
            if (filesize != null && info != null) {
                viewHolder.time!!.text = "$filesize \u00B7 $info$bodyLanguageInfo"
            } else if (filesize == null && info != null) {
                if (error) {
                    viewHolder.time!!.text = "$info \u00B7 $formattedTime$bodyLanguageInfo"
                } else {
                    viewHolder.time!!.text = info
                }
            } else if (filesize != null && info == null) {
                viewHolder.time!!.text = "$filesize \u00B7 $formattedTime$bodyLanguageInfo"
            } else {
                viewHolder.time!!.text = formattedTime + bodyLanguageInfo
            }
        }
    }

    private fun displayInfoMessage(viewHolder: ViewHolder, text: CharSequence, darkBackground: Boolean) {
        viewHolder.download_button?.visibility = View.GONE
        viewHolder.audioPlayer?.visibility = View.GONE
        viewHolder.image?.visibility = View.GONE
        viewHolder.messageBody?.visibility = View.VISIBLE
        viewHolder.messageBody?.text = text
        if (darkBackground) {
            viewHolder.messageBody!!.setTextAppearance(
                context,
                R.style.TextAppearance_Conversations_Body1_Secondary_OnDark
            )
        } else {
            viewHolder.messageBody!!.setTextAppearance(context, R.style.TextAppearance_Conversations_Body1_Secondary)
        }
        viewHolder.messageBody!!.setTextIsSelectable(false)
    }

    private fun displayEmojiMessage(viewHolder: ViewHolder, body: String, darkBackground: Boolean) {
        viewHolder.download_button?.visibility = View.GONE
        viewHolder.audioPlayer?.visibility = View.GONE
        viewHolder.image?.visibility = View.GONE
        viewHolder.messageBody?.visibility = View.VISIBLE
        if (darkBackground) {
            viewHolder.messageBody?.setTextAppearance(context, R.style.TextAppearance_Conversations_Body1_Emoji_OnDark)
        } else {
            viewHolder.messageBody?.setTextAppearance(context, R.style.TextAppearance_Conversations_Body1_Emoji)
        }
        val span: Spannable = SpannableString(body)
        val size = if (Emoticons.isEmoji(body)) 3.0f else 2.0f
        span.setSpan(RelativeSizeSpan(size), 0, body.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        viewHolder.messageBody?.text = span
    }

    private fun applyQuoteSpan(body: SpannableStringBuilder, start: Int, end: Int, darkBackground: Boolean) {
        var start = start
        var end = end
        if (start > 1 && "\n\n" != body.subSequence(start - 2, start).toString()) {
            body.insert(start++, "\n")
            body.setSpan(DividerSpan(false), start - 2, start, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            end++
        }
        if (end < body.length - 1 && "\n\n" != body.subSequence(end, end + 2).toString()) {
            body.insert(end, "\n")
            body.setSpan(DividerSpan(false), end, end + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        val color = if (darkBackground) getMessageTextColor(darkBackground, false) else ContextCompat.getColor(
            activity,
            R.color.green700_desaturated
        )
        val metrics = context.resources.displayMetrics
        body.setSpan(QuoteSpan(color, metrics), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    /**
     * Applies QuoteSpan to group of lines which starts with > or » characters.
     * Appends likebreaks and applies DividerSpan to them to show a padding between quote and text.
     */
    private fun handleTextQuotes(body: SpannableStringBuilder, darkBackground: Boolean): Boolean {
        var startsWithQuote = false
        var quoteDepth = 1
        while (QuoteHelper.bodyContainsQuoteStart(body) && quoteDepth <= Config.QUOTE_MAX_DEPTH) {
            var previous = '\n'
            var lineStart = -1
            var lineTextStart = -1
            var quoteStart = -1
            var i = 0
            while (i <= body.length) {
                val current = if (body.length > i) body[i] else '\n'
                if (lineStart == -1) {
                    if (previous == '\n') {
                        if (i < body.length && QuoteHelper.isPositionQuoteStart(body, i)) {
                            // Line start with quote
                            lineStart = i
                            if (quoteStart == -1) quoteStart = i
                            if (i == 0) startsWithQuote = true
                        } else if (quoteStart >= 0) {
                            // Line start without quote, apply spans there
                            applyQuoteSpan(body, quoteStart, i - 1, darkBackground)
                            quoteStart = -1
                        }
                    }
                } else {
                    // Remove extra spaces between > and first character in the line
                    // > character will be removed too
                    if (current != ' ' && lineTextStart == -1) {
                        lineTextStart = i
                    }
                    if (current == '\n') {
                        body.delete(lineStart, lineTextStart)
                        i -= lineTextStart - lineStart
                        if (i == lineStart) {
                            // Avoid empty lines because span over empty line can be hidden
                            body.insert(i++, " ")
                        }
                        lineStart = -1
                        lineTextStart = -1
                    }
                }
                previous = current
                i++
            }
            if (quoteStart >= 0) {
                // Apply spans to finishing open quote
                applyQuoteSpan(body, quoteStart, body.length, darkBackground)
            }
            quoteDepth++
        }
        return startsWithQuote
    }

    private fun displayTextMessage(viewHolder: ViewHolder, message: Message?, darkBackground: Boolean, type: Int) {
        viewHolder.download_button?.visibility = View.GONE
        viewHolder.image?.visibility = View.GONE
        viewHolder.audioPlayer?.visibility = View.GONE
        viewHolder.messageBody?.visibility = View.VISIBLE
        if (darkBackground) {
            viewHolder.messageBody?.setTextAppearance(context, R.style.TextAppearance_Conversations_Body1_OnDark)
        } else {
            viewHolder.messageBody?.setTextAppearance(context, R.style.TextAppearance_Conversations_Body1)
        }
        viewHolder.messageBody!!.highlightColor = ContextCompat.getColor(
            activity,
            if (darkBackground) if (type == SENT || !mUseGreenBackground) R.color.black26 else R.color.grey800 else R.color.grey500
        )
        viewHolder.messageBody?.setTypeface(null, Typeface.NORMAL)
        if (message?.body != null) {
            val nick = UIHelper.getMessageDisplayName(message)
            var body = message.mergedBody
            val hasMeCommand = message.hasMeCommand()
            if (hasMeCommand) {
                body = body.replace(0, Message.ME_COMMAND.length, "$nick ")
            }
            if (body.length > Config.MAX_DISPLAY_MESSAGE_CHARS) {
                body = SpannableStringBuilder(body, 0, Config.MAX_DISPLAY_MESSAGE_CHARS)
                body.append("\u2026")
            }
            val mergeSeparators = body.getSpans(0, body.length, MergeSeparator::class.java)
            for (mergeSeparator in mergeSeparators) {
                val start = body.getSpanStart(mergeSeparator)
                val end = body.getSpanEnd(mergeSeparator)
                body.setSpan(DividerSpan(true), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            val startsWithQuote = handleTextQuotes(body, darkBackground)
            if (!message.isPrivateMessage) {
                if (hasMeCommand) {
                    body.setSpan(
                        StyleSpan(Typeface.BOLD_ITALIC), 0, nick.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            } else {
                val privateMarker: String = if (message.status <= Message.STATUS_RECEIVED) {
                    activity.getString(R.string.private_message)
                } else {
                    val cp = message.counterpart
                    activity.getString(R.string.private_message_to, Strings.nullToEmpty(cp?.resource))
                }
                body.insert(0, privateMarker)
                val privateMarkerIndex = privateMarker.length
                if (startsWithQuote) {
                    body.insert(privateMarkerIndex, "\n\n")
                    body.setSpan(
                        DividerSpan(false), privateMarkerIndex, privateMarkerIndex + 2,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                } else {
                    body.insert(privateMarkerIndex, " ")
                }
                body.setSpan(
                    ForegroundColorSpan(getMessageTextColor(darkBackground, false)),
                    0,
                    privateMarkerIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                body.setSpan(StyleSpan(Typeface.BOLD), 0, privateMarkerIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                if (hasMeCommand) {
                    body.setSpan(
                        StyleSpan(Typeface.BOLD_ITALIC), privateMarkerIndex + 1,
                        privateMarkerIndex + 1 + nick.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
            if (message.conversation.mode == Conversation.MODE_MULTI && message.status == Message.STATUS_RECEIVED) {
                if (message.conversation is Conversation) {
                    val conversation = message.conversation as Conversation
                    val pattern = NotificationService.generateNickHighlightPattern(conversation.mucOptions.actualNick)
                    val matcher = pattern.matcher(body)
                    while (matcher.find()) {
                        body.setSpan(
                            StyleSpan(Typeface.BOLD),
                            matcher.start(),
                            matcher.end(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                }
            }
            val matcher = Emoticons.getEmojiPattern(body).matcher(body)
            while (matcher.find()) {
                if (matcher.start() < matcher.end()) {
                    body.setSpan(
                        RelativeSizeSpan(1.2f),
                        matcher.start(),
                        matcher.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
            StylingHelper.format(body, viewHolder.messageBody!!.currentTextColor)
            if (highlightedTerm != null) {
                StylingHelper.highlight(
                    activity,
                    body,
                    highlightedTerm,
                    StylingHelper.isDarkText(viewHolder.messageBody)
                )
            }
            MyLinkify.addLinks(body, true)
            viewHolder.messageBody?.autoLinkMask = 0
            viewHolder.messageBody?.text = body
            viewHolder.messageBody?.movementMethod = ClickableMovementMethod.getInstance()
        } else {
            viewHolder.messageBody?.text = ""
            viewHolder.messageBody?.setTextIsSelectable(false)
        }
    }

    private fun displayDownloadableMessage(
        viewHolder: ViewHolder,
        message: Message?,
        text: String,
        darkBackground: Boolean
    ) {
        toggleWhisperInfo(viewHolder, message, darkBackground)
        viewHolder.image!!.visibility = View.GONE
        viewHolder.audioPlayer!!.visibility = View.GONE
        viewHolder.download_button!!.visibility = View.VISIBLE
        viewHolder.download_button!!.text = text
        viewHolder.download_button!!.setOnClickListener { v: View? ->
            ConversationFragment.downloadFile(
                activity,
                message
            )
        }
    }

    private fun displayOpenableMessage(viewHolder: ViewHolder, message: Message?, darkBackground: Boolean) {
        toggleWhisperInfo(viewHolder, message, darkBackground)
        viewHolder.image!!.visibility = View.GONE
        viewHolder.audioPlayer!!.visibility = View.GONE
        viewHolder.download_button!!.visibility = View.VISIBLE
        viewHolder.download_button!!.text =
            activity.getString(R.string.open_x_file, UIHelper.getFileDescriptionString(activity, message))
        viewHolder.download_button!!.setOnClickListener { v: View? -> openDownloadable(message) }
    }

    private fun displayLocationMessage(viewHolder: ViewHolder, message: Message?, darkBackground: Boolean) {
        toggleWhisperInfo(viewHolder, message, darkBackground)
        viewHolder.image!!.visibility = View.GONE
        viewHolder.audioPlayer!!.visibility = View.GONE
        viewHolder.download_button!!.visibility = View.VISIBLE
        viewHolder.download_button!!.setText(R.string.show_location)
        viewHolder.download_button!!.setOnClickListener { v: View? -> showLocation(message) }
    }

    private fun displayAudioMessage(viewHolder: ViewHolder, message: Message?, darkBackground: Boolean) {
        toggleWhisperInfo(viewHolder, message, darkBackground)
        viewHolder.image!!.visibility = View.GONE
        viewHolder.download_button!!.visibility = View.GONE
        val audioPlayer = viewHolder.audioPlayer
        audioPlayer!!.visibility = View.VISIBLE
        AudioPlayer.ViewHolder.get(audioPlayer).setDarkBackground(darkBackground)
        this.audioPlayer.init(audioPlayer, message)
    }

    private fun displayMediaPreviewMessage(viewHolder: ViewHolder, message: Message?, darkBackground: Boolean) {
        toggleWhisperInfo(viewHolder, message, darkBackground)
        viewHolder.download_button?.visibility = View.GONE
        viewHolder.audioPlayer?.visibility = View.GONE
        viewHolder.image?.visibility = View.VISIBLE
        val params = message!!.fileParams
        val target = activity.resources.getDimension(R.dimen.image_preview_width)
        val scaledW: Int
        val scaledH: Int
        if (Math.max(params.height, params.width) * metrics.density <= target) {
            scaledW = (params.width * metrics.density).toInt()
            scaledH = (params.height * metrics.density).toInt()
        } else if (Math.max(params.height, params.width) <= target) {
            scaledW = params.width
            scaledH = params.height
        } else if (params.width <= params.height) {
            scaledW = (params.width / (params.height.toDouble() / target)).toInt()
            scaledH = target.toInt()
        } else {
            scaledW = target.toInt()
            scaledH = (params.height / (params.width.toDouble() / target)).toInt()
        }
        val layoutParams = LinearLayout.LayoutParams(scaledW, scaledH)
        layoutParams.setMargins(0, (metrics.density * 4).toInt(), 0, (metrics.density * 4).toInt())
        viewHolder.image!!.layoutParams = layoutParams
        activity.loadBitmap(message, viewHolder.image)
        viewHolder.image!!.setOnClickListener { v: View? -> openDownloadable(message) }
    }

    private fun toggleWhisperInfo(viewHolder: ViewHolder, message: Message?, darkBackground: Boolean) {
        if (message!!.isPrivateMessage) {
            val privateMarker: String
            privateMarker = if (message.status <= Message.STATUS_RECEIVED) {
                activity.getString(R.string.private_message)
            } else {
                val cp = message.counterpart
                activity.getString(R.string.private_message_to, Strings.nullToEmpty(cp?.resource))
            }
            val body = SpannableString(privateMarker)
            body.setSpan(
                ForegroundColorSpan(getMessageTextColor(darkBackground, false)),
                0,
                privateMarker.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            body.setSpan(StyleSpan(Typeface.BOLD), 0, privateMarker.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            viewHolder.messageBody!!.text = body
            viewHolder.messageBody!!.visibility = View.VISIBLE
        } else {
            viewHolder.messageBody!!.visibility = View.GONE
        }
    }

    private fun loadMoreMessages(conversation: Conversation) {
        conversation.setLastClearHistory(0, null)
        activity.xmppConnectionService.updateConversation(conversation)
        conversation.setHasMessagesLeftOnServer(true)
        conversation.firstMamReference = null
        var timestamp = conversation.lastMessageTransmitted.timestamp
        if (timestamp == 0L) {
            timestamp = System.currentTimeMillis()
        }
        conversation.messagesLoaded.set(true)
        val query =
            activity.xmppConnectionService.messageArchiveService.query(conversation, MamReference(0), timestamp, false)
        if (query != null) {
            Toast.makeText(activity, R.string.fetching_history_from_server, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(activity, R.string.not_fetching_history_retention_period, Toast.LENGTH_SHORT).show()
        }
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var view = view
        val message = getItem(position)
        val omemoEncryption = message!!.encryption == Message.ENCRYPTION_AXOLOTL
        val isInValidSession = message.isValidInSession && (!omemoEncryption || message.isTrusted)
        val conversation = message.conversation
        val account = conversation.account
        val type = getItemViewType(position)
        val viewHolder: ViewHolder
        if (view == null) {
            viewHolder = ViewHolder()
            when (type) {
                DATE_SEPARATOR -> {
                    view = activity.layoutInflater.inflate(R.layout.message_date_bubble, parent, false)
                    viewHolder.status_message = view.findViewById(R.id.message_body)
                    viewHolder.message_box = view.findViewById(R.id.message_box)
                }
                RTP_SESSION -> {
                    view = activity.layoutInflater.inflate(R.layout.message_rtp_session, parent, false)
                    viewHolder.status_message = view.findViewById(R.id.message_body)
                    viewHolder.message_box = view.findViewById(R.id.message_box)
                }
                SENT -> {
                    view = activity.layoutInflater.inflate(R.layout.message_sent, parent, false)
                    viewHolder.message_box = view.findViewById<LinearLayout>(R.id.message_box)
                    viewHolder.contact_picture = view.findViewById(R.id.message_photo)
                    viewHolder.download_button = view.findViewById(R.id.download_button)
                    viewHolder.indicator = view.findViewById(R.id.security_indicator)
                    viewHolder.edit_indicator = view.findViewById(R.id.edit_indicator)
                    viewHolder.image = view.findViewById(R.id.message_image)
                    viewHolder.messageBody = view.findViewById(R.id.message_body)
                    viewHolder.time = view.findViewById(R.id.message_time)
                    viewHolder.indicatorReceived = view.findViewById(R.id.indicator_received)
                    viewHolder.audioPlayer = view.findViewById(R.id.audio_player)
                }
                RECEIVED -> {
                    view = activity.layoutInflater.inflate(R.layout.message_received, parent, false)
                    viewHolder.message_box = view.findViewById(R.id.message_box)
                    viewHolder.contact_picture = view.findViewById(R.id.message_photo)
                    viewHolder.download_button = view.findViewById(R.id.download_button)
                    viewHolder.indicator = view.findViewById(R.id.security_indicator)
                    viewHolder.edit_indicator = view.findViewById(R.id.edit_indicator)
                    viewHolder.image = view.findViewById(R.id.message_image)
                    viewHolder.messageBody = view.findViewById(R.id.message_body)
                    viewHolder.time = view.findViewById(R.id.message_time)
                    //viewHolder.indicatorReceived = view.findViewById(R.id.indicator_received);
                    viewHolder.encryption = view.findViewById(R.id.message_encryption)
                    viewHolder.audioPlayer = view.findViewById(R.id.audio_player)
                }
                STATUS -> {
                    view = activity.layoutInflater.inflate(R.layout.message_status, parent, false)
                    viewHolder.contact_picture = view.findViewById(R.id.message_photo)
                    viewHolder.status_message = view.findViewById(R.id.status_message)
                    viewHolder.load_more_messages = view.findViewById(R.id.load_more_messages)
                }
                else -> throw AssertionError("Unknown view type")
            }
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
            if (viewHolder == null) {
                return view
            }
        }
        val darkBackground = activity.isDarkTheme
        if (type == DATE_SEPARATOR) {
            if (UIHelper.today(message.timeSent)) {
                viewHolder.status_message!!.setText(R.string.today)
            } else if (UIHelper.yesterday(message.timeSent)) {
                viewHolder.status_message!!.setText(R.string.yesterday)
            } else {
                viewHolder.status_message!!.text = DateUtils.formatDateTime(
                    activity,
                    message.timeSent,
                    DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR
                )
            }
            viewHolder.message_box!!.setBackgroundResource(if (activity.isDarkTheme) R.drawable.date_bubble_grey else R.drawable.date_bubble_white)
            return view!!
        } else if (type == RTP_SESSION) {
            val isDarkTheme = activity.isDarkTheme
            val received = message.status <= Message.STATUS_RECEIVED
            val rtpSessionStatus = RtpSessionStatus.of(message.body)
            val duration = rtpSessionStatus.duration
            if (received) {
                if (duration > 0) {
                    viewHolder.status_message?.text =
                        activity.getString(R.string.incoming_call_duration, TimeFrameUtils.resolve(activity, duration))
                } else if (rtpSessionStatus.successful) {
                    viewHolder.status_message?.setText(R.string.incoming_call)
                } else {
                    viewHolder.status_message?.text = activity.getString(
                        R.string.missed_call_timestamp,
                        UIHelper.readableTimeDifferenceFull(activity, message.timeSent)
                    )
                }
            } else {
                if (duration > 0) {
                    viewHolder.status_message?.text =
                        activity.getString(R.string.outgoing_call_duration, TimeFrameUtils.resolve(activity, duration))
                } else {
                    viewHolder.status_message?.setText(R.string.outgoing_call)
                }
            }
            //viewHolder.indicatorReceived.setImageResource(RtpSessionStatus.getDrawable(received, rtpSessionStatus.successful, isDarkTheme));
            viewHolder.indicatorReceived!!.alpha = if (isDarkTheme) 0.7f else 0.57f
            viewHolder.message_box!!.setBackgroundResource(if (isDarkTheme) R.drawable.date_bubble_grey else R.drawable.date_bubble_white)
            return view!!
        } else if (type == STATUS) {
            if ("LOAD_MORE" == message.body) {
                viewHolder.status_message?.visibility = View.GONE
                viewHolder.contact_picture?.visibility = View.GONE
                viewHolder.load_more_messages?.visibility = View.VISIBLE
                viewHolder.load_more_messages?.setOnClickListener { v: View? ->
                    loadMoreMessages(
                        message.conversation as Conversation
                    )
                }
            } else {
                viewHolder.status_message?.visibility = View.VISIBLE
                viewHolder.load_more_messages?.visibility = View.GONE
                viewHolder.status_message?.text = message.body
                val showAvatar: Boolean
                if (conversation.mode == Conversation.MODE_SINGLE) {
                    showAvatar = true
                    AvatarWorkerTask.loadAvatar(message, viewHolder.contact_picture, R.dimen.avatar_on_status_message)
                } else if (message.counterpart != null || message.trueCounterpart != null || message.counterparts != null && message.counterparts.size > 0) {
                    showAvatar = true
                    AvatarWorkerTask.loadAvatar(message, viewHolder.contact_picture, R.dimen.avatar_on_status_message)
                } else {
                    showAvatar = false
                }
                if (showAvatar) {
                    viewHolder.contact_picture?.alpha = 0.5f
                    viewHolder.contact_picture?.visibility = View.VISIBLE
                } else {
                    viewHolder.contact_picture?.visibility = View.GONE
                }
            }
            return view!!
        } else {
            AvatarWorkerTask.loadAvatar(message, viewHolder.contact_picture, R.dimen.avatar);
        }
        resetClickListener(viewHolder.message_box!!, viewHolder.messageBody!!)
        viewHolder.contact_picture?.setOnClickListener { v: View ->
            if (mOnContactPictureClickedListener != null) {
                mOnContactPictureClickedListener!!
                    .onContactPictureClicked(message)
            }
        }
        viewHolder.contact_picture?.setOnLongClickListener { v: View ->
            if (mOnContactPictureLongClickedListener != null) {
                mOnContactPictureLongClickedListener!!
                    .onContactPictureLongClicked(v, message)
                return@setOnLongClickListener true
            } else {
                return@setOnLongClickListener false
            }
        }
        val transferable = message.transferable
        val unInitiatedButKnownSize = MessageUtils.unInitiatedButKnownSize(message)
        if (unInitiatedButKnownSize || message.isDeleted || transferable != null && transferable.status != Transferable.STATUS_UPLOADING) {
            if (unInitiatedButKnownSize || transferable != null && transferable.status == Transferable.STATUS_OFFER) {
                displayDownloadableMessage(
                    viewHolder,
                    message,
                    activity.getString(
                        R.string.download_x_file,
                        UIHelper.getFileDescriptionString(activity, message)
                    ),
                    darkBackground
                )
            } else if (transferable != null && transferable.status == Transferable.STATUS_OFFER_CHECK_FILESIZE) {
                displayDownloadableMessage(
                    viewHolder,
                    message,
                    activity.getString(
                        R.string.check_x_filesize,
                        UIHelper.getFileDescriptionString(activity, message)
                    ),
                    darkBackground
                )
            } else {
                displayInfoMessage(viewHolder, UIHelper.getMessagePreview(activity, message).first, darkBackground)
            }
        } else if (message.isFileOrImage && message.encryption != Message.ENCRYPTION_PGP && message.encryption != Message.ENCRYPTION_DECRYPTION_FAILED) {
            if (message.fileParams.width > 0 && message.fileParams.height > 0) {
                displayMediaPreviewMessage(viewHolder, message, darkBackground)
            } else if (message.fileParams.runtime > 0) {
                displayAudioMessage(viewHolder, message, darkBackground)
            } else {
                displayOpenableMessage(viewHolder, message, darkBackground)
            }
        } else if (message.encryption == Message.ENCRYPTION_PGP) {
            if (account.isPgpDecryptionServiceConnected) {
                if (conversation is Conversation && !account.hasPendingPgpIntent(conversation)) {
                    displayInfoMessage(viewHolder, activity.getString(R.string.message_decrypting), darkBackground)
                } else {
                    displayInfoMessage(viewHolder, activity.getString(R.string.pgp_message), darkBackground)
                }
            } else {
                displayInfoMessage(viewHolder, activity.getString(R.string.install_openkeychain), darkBackground)
                viewHolder.message_box!!.setOnClickListener { view: View -> promptOpenKeychainInstall(view) }
                viewHolder.messageBody!!.setOnClickListener { view: View -> promptOpenKeychainInstall(view) }
            }
        } else if (message.encryption == Message.ENCRYPTION_DECRYPTION_FAILED) {
            displayInfoMessage(viewHolder, activity.getString(R.string.decryption_failed), darkBackground)
        } else if (message.encryption == Message.ENCRYPTION_AXOLOTL_NOT_FOR_THIS_DEVICE) {
            displayInfoMessage(viewHolder, activity.getString(R.string.not_encrypted_for_this_device), darkBackground)
        } else if (message.encryption == Message.ENCRYPTION_AXOLOTL_FAILED) {
            displayInfoMessage(viewHolder, activity.getString(R.string.omemo_decryption_failed), darkBackground)
        } else {
            if (message.isGeoUri) {
                displayLocationMessage(viewHolder, message, darkBackground)
            } else if (message.bodyIsOnlyEmojis() && message.type != Message.TYPE_PRIVATE) {
                displayEmojiMessage(viewHolder, message.body.trim { it <= ' ' }, darkBackground)
            } else if (message.treatAsDownloadable()) {
                try {
                    val uri = URI(message.body)
                    displayDownloadableMessage(
                        viewHolder,
                        message,
                        activity.getString(
                            R.string.check_x_filesize_on_host,
                            UIHelper.getFileDescriptionString(activity, message),
                            uri.host
                        ),
                        darkBackground
                    )
                } catch (e: Exception) {
                    displayDownloadableMessage(
                        viewHolder,
                        message,
                        activity.getString(
                            R.string.check_x_filesize,
                            UIHelper.getFileDescriptionString(activity, message)
                        ),
                        darkBackground
                    )
                }
            } else {
                displayTextMessage(viewHolder, message, darkBackground, type)
            }
        }
        if (type == RECEIVED) {
            viewHolder.message_box?.setBackgroundResource(com.topiichat.app.R.drawable.bg_chat_incoming_message)
            if (isInValidSession) {
                viewHolder.encryption?.visibility = View.GONE
            } else {
                viewHolder.encryption?.visibility = View.VISIBLE
                if (omemoEncryption && !message.isTrusted) {
                    viewHolder.encryption?.setText(R.string.not_trusted)
                } else {
                    viewHolder.encryption?.setText(CryptoHelper.encryptionTypeToText(message.encryption))
                }
            }
        }

        if (type == SENT) {
            viewHolder.message_box?.setBackgroundResource(com.topiichat.app.R.drawable.bg_chat_outgoing_message)
        }

        displayStatus(viewHolder, message, type, darkBackground);
        return view!!
    }

    private fun promptOpenKeychainInstall(view: View) {
        activity.showInstallPgpDialog()
    }

    val fileBackend: FileBackend
        get() = activity.xmppConnectionService.fileBackend

    fun stopAudioPlayer() {
        audioPlayer.stop()
    }

    fun unregisterListenerInAudioPlayer() {
        audioPlayer.unregisterListener()
    }

    fun startStopPending() {
        audioPlayer.startStopPending()
    }

    fun openDownloadable(message: Message?) {
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ConversationFragment.registerPendingMessage(activity, message)
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                ConversationsActivity.REQUEST_OPEN_MESSAGE
            )
            return
        }
        val file = activity.xmppConnectionService.fileBackend.getFile(message)
        ViewUtil.view(activity, file)
    }

    private fun showLocation(message: Message?) {
        for (intent in GeoHelper.createGeoIntentsFromMessage(activity, message)) {
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
                return
            }
        }
        Toast.makeText(activity, R.string.no_application_found_to_display_location, Toast.LENGTH_SHORT).show()
    }

    fun updatePreferences() {
        val p = PreferenceManager.getDefaultSharedPreferences(activity)
        mUseGreenBackground =
            p.getBoolean("use_green_background", activity.resources.getBoolean(R.bool.use_green_background))
    }

    fun setHighlightedTerm(terms: List<String?>?) {
        highlightedTerm = if (terms == null) null else StylingHelper.filterHighlightedWords(terms)
    }

    interface OnContactPictureClicked {
        fun onContactPictureClicked(message: Message)
    }

    interface OnContactPictureLongClicked {
        fun onContactPictureLongClicked(v: View, message: Message)
    }

    private class ViewHolder {
        var load_more_messages: Button? = null
        var edit_indicator: ImageView? = null
        var audioPlayer: RelativeLayout? = null
        var message_box: LinearLayout? = null
        var download_button: Button? = null
        var image: ImageView? = null
        var indicator: ImageView? = null
        var indicatorReceived: ImageView? = null
        var time: TextView? = null
        var messageBody: TextView? = null
        var contact_picture: ImageView? = null
        var status_message: TextView? = null
        var encryption: TextView? = null
    }

    companion object {
        const val DATE_SEPARATOR_BODY = "DATE_SEPARATOR"
        private const val SENT = 0
        private const val RECEIVED = 1
        private const val STATUS = 2
        private const val DATE_SEPARATOR = 3
        private const val RTP_SESSION = 4
        private fun resetClickListener(vararg views: View) {
            for (view in views) {
                view.setOnClickListener(null)
            }
        }
    }

    init {
        this.activity = activity
        metrics = context.resources.displayMetrics
        updatePreferences()
        mForceNames = forceNames
    }
}