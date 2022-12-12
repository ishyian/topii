package com.topiichat.chat.chat

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.text.TextUtils
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AbsListView
import android.widget.AdapterView.AdapterContextMenuInfo
import android.widget.CheckBox
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.inputmethod.InputConnectionCompat
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.common.collect.ImmutableList
import com.topiichat.chat.activity.ChatsActivity
import com.topiichat.chat.base.BaseChatFragment
import com.topiichat.chat.chat.adapter.MediaPreviewAdapter
import com.topiichat.chat.chat.adapter.MessageAdapter
import com.topiichat.chat.rtc.RtpSessionActivity
import com.topiichat.core.extension.viewModelCreator
import com.yourbestigor.chat.R
import com.yourbestigor.chat.databinding.DialogAddAttachmentBinding
import com.yourbestigor.chat.databinding.FragmentChatBinding
import dagger.hilt.android.AndroidEntryPoint
import eu.siacs.conversations.Config
import eu.siacs.conversations.crypto.axolotl.FingerprintStatus
import eu.siacs.conversations.entities.Account
import eu.siacs.conversations.entities.Blockable
import eu.siacs.conversations.entities.Contact
import eu.siacs.conversations.entities.Conversation
import eu.siacs.conversations.entities.Conversational
import eu.siacs.conversations.entities.Message
import eu.siacs.conversations.entities.MucOptions
import eu.siacs.conversations.entities.Presence
import eu.siacs.conversations.entities.ReadByMarker
import eu.siacs.conversations.entities.Transferable
import eu.siacs.conversations.entities.TransferablePlaceholder
import eu.siacs.conversations.http.HttpDownloadConnection
import eu.siacs.conversations.persistance.FileBackend
import eu.siacs.conversations.services.QuickConversationsService
import eu.siacs.conversations.services.XmppConnectionService.OnMoreMessagesLoaded
import eu.siacs.conversations.ui.BlockContactDialog
import eu.siacs.conversations.ui.TrustKeysActivity
import eu.siacs.conversations.ui.UiCallback
import eu.siacs.conversations.ui.UiInformableCallback
import eu.siacs.conversations.ui.XmppActivity
import eu.siacs.conversations.ui.XmppActivity.ConferenceInvite
import eu.siacs.conversations.ui.util.ActivityResult
import eu.siacs.conversations.ui.util.Attachment
import eu.siacs.conversations.ui.util.DateSeparator
import eu.siacs.conversations.ui.util.EditMessageActionModeCallback
import eu.siacs.conversations.ui.util.ListViewUtils
import eu.siacs.conversations.ui.util.MucDetailsContextMenuHelper
import eu.siacs.conversations.ui.util.PendingItem
import eu.siacs.conversations.ui.util.PresenceSelector
import eu.siacs.conversations.ui.util.PresenceSelector.OnPresenceSelected
import eu.siacs.conversations.ui.util.ScrollState
import eu.siacs.conversations.ui.util.SendButtonAction
import eu.siacs.conversations.ui.util.SendButtonTool
import eu.siacs.conversations.ui.util.ShareUtil
import eu.siacs.conversations.ui.util.SoftKeyboardUtils
import eu.siacs.conversations.ui.util.ViewUtil
import eu.siacs.conversations.ui.widget.EditMessage
import eu.siacs.conversations.utils.AccountUtils
import eu.siacs.conversations.utils.Compatibility
import eu.siacs.conversations.utils.GeoHelper
import eu.siacs.conversations.utils.MessageUtils
import eu.siacs.conversations.utils.NickValidityChecker
import eu.siacs.conversations.utils.PermissionUtils
import eu.siacs.conversations.utils.QuickLoader
import eu.siacs.conversations.utils.StylingHelper.MessageEditorStyler
import eu.siacs.conversations.utils.UIHelper
import eu.siacs.conversations.xml.Namespace
import eu.siacs.conversations.xmpp.Jid
import eu.siacs.conversations.xmpp.chatstate.ChatState
import eu.siacs.conversations.xmpp.jingle.AbstractJingleConnection.Id
import eu.siacs.conversations.xmpp.jingle.JingleConnectionManager.RtpSessionProposal
import eu.siacs.conversations.xmpp.jingle.JingleFileTransferConnection
import eu.siacs.conversations.xmpp.jingle.Media
import eu.siacs.conversations.xmpp.jingle.RtpCapability
import timber.log.Timber
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import kotlin.math.max

@AndroidEntryPoint
class ChatFragment : BaseChatFragment<FragmentChatBinding>(),
    IChatFragment,
    EditMessage.KeyboardListener,
    MessageAdapter.OnContactPictureLongClicked,
    MessageAdapter.OnContactPictureClicked {

    @Inject
    lateinit var factory: ChatViewModel.AssistedFactory
    private val viewModel by viewModelCreator {
        factory.create()
    }

    private val messageList: MutableList<Message> = ArrayList()
    private val postponedActivityResult = PendingItem<ActivityResult>()
    private val pendingConversationsUuid = PendingItem<String>()
    private val pendingMediaPreviews = PendingItem<ArrayList<Attachment>>()
    private val pendingExtras = PendingItem<Bundle?>()
    private val pendingTakePhotoUri = PendingItem<Uri>()
    private val pendingScrollState = PendingItem<ScrollState?>()
    private val pendingLastMessageUuid = PendingItem<String>()
    private val pendingMessage = PendingItem<Message>()
    var mPendingEditorContent: Uri? = null
    private var messageListAdapter: MessageAdapter? = null
    private var mediaPreviewAdapter: MediaPreviewAdapter? = null
    private var lastMessageUuid: String? = null
    var conversation: Conversation? = null
        private set
    private var messageLoaderToast: Toast? = null
    private var chatsActivity: ChatsActivity? = null
    private var reInitRequiredOnStart = true

    private var completionIndex = 0
    private var lastCompletionLength = 0
    private var incomplete: String? = null
    private var lastCompletionCursor = 0
    private var firstWord = false
    private var pendingDownloadableMessage: Message? = null

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentChatBinding {
        return FragmentChatBinding.inflate(inflater, container, false)
    }

    private val onScrollListener: AbsListView.OnScrollListener = object : AbsListView.OnScrollListener {
        override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
            if (AbsListView.OnScrollListener.SCROLL_STATE_IDLE == scrollState) {
                fireReadEvent()
            }
        }

        override fun onScroll(
            view: AbsListView,
            firstVisibleItem: Int,
            visibleItemCount: Int,
            totalItemCount: Int
        ) {
            toggleScrollDownButton(view)
            synchronized(messageList) {
                if (firstVisibleItem < 5
                    && conversation != null
                    && conversation!!.messagesLoaded.compareAndSet(true, false)
                    && messageList.size > 0
                ) {
                    val timestamp: Long = if (messageList[0].type == Message.TYPE_STATUS
                        && messageList.size >= 2
                    ) {
                        messageList[1].timeSent
                    } else {
                        messageList[0].timeSent
                    }
                    chatsActivity?.xmppConnectionService?.loadMoreMessages(
                        conversation,
                        timestamp,
                        onMoreMessagesLoaded
                    )
                }
            }
        }
    }

    private val onMoreMessagesLoaded = object : OnMoreMessagesLoaded {
        override fun onMoreMessagesLoaded(c: Int, conversation: Conversation) {
            if (this@ChatFragment.conversation !== conversation) {
                conversation.messagesLoaded.set(true)
                return
            }
            runOnUiThread {
                synchronized(messageList) {
                    val oldPosition =
                        binding.rvMessagesList
                            .firstVisiblePosition
                    var message: Message? = null
                    var childPos = 0
                    while (childPos + oldPosition < messageList.size) {
                        message = messageList[oldPosition + childPos]
                        if (message.type != Message.TYPE_STATUS) {
                            break
                        }
                        ++childPos
                    }
                    val uuid = message?.uuid
                    val v = binding.rvMessagesList.getChildAt(childPos)
                    val pxOffset = v?.top ?: 0
                    this@ChatFragment.conversation!!.populateWithMessages(messageList)
                    try {
                        updateStatusMessages()
                    } catch (e: IllegalStateException) {
                        Timber.d("caught illegal state exception while updating status messages")
                    }
                    messageListAdapter?.notifyDataSetChanged()
                    val pos = max(getIndexOf(uuid, messageList), 0)
                    binding.rvMessagesList.setSelectionFromTop(pos, pxOffset)
                    if (messageLoaderToast != null) {
                        messageLoaderToast!!.cancel()
                    }
                    conversation.messagesLoaded.set(
                        true
                    )
                }
            }
        }

        override fun informUser(resId: Int) {
            runOnUiThread {
                if (messageLoaderToast != null) {
                    messageLoaderToast!!.cancel()
                }
                if (conversation !== conversation) {
                    return@runOnUiThread
                }
                messageLoaderToast = Toast.makeText(requireContext(), resId, Toast.LENGTH_LONG)
                messageLoaderToast?.show()
            }
        }
    }

    private val editorContentListener =
        EditMessage.OnCommitContentListener { inputContentInfo, flags, _, _ -> // try to get permission to read the image, if applicable
            if (flags and InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION
                != 0
            ) {
                try {
                    inputContentInfo.requestPermission()
                } catch (e: Exception) {
                    Timber.e(e, "InputContentInfoCompat#requestPermission() failed.")
                    Toast.makeText(
                        activity,
                        chatsActivity!!.getString(
                            R.string.no_permission_to_access_x,
                            inputContentInfo.description
                        ),
                        Toast.LENGTH_LONG
                    )
                        .show()
                    return@OnCommitContentListener false
                }
            }
            if (hasPermissions(
                    REQUEST_ADD_EDITOR_CONTENT,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                attachEditorContentToConversation(inputContentInfo.contentUri)
            } else {
                mPendingEditorContent = inputContentInfo.contentUri
            }
            true
        }
    private var selectedMessage: Message? = null
    private val mEnableAccountListener = View.OnClickListener {
        val account = if (conversation == null) null else conversation!!.account
        if (account != null) {
            account.setOption(Account.OPTION_DISABLED, false)
            chatsActivity!!.xmppConnectionService.updateAccount(account)
        }
    }
    private val mUnblockClickListener = View.OnClickListener { v ->
        v.post { v.visibility = View.INVISIBLE }
        if (conversation!!.isDomainBlocked) {
            BlockContactDialog.show(chatsActivity, conversation)
        } else {
            unblockConversation(conversation)
        }
    }
    private val mBlockClickListener = View.OnClickListener { view: View -> showBlockSubmenu(view) }
    private val mAddBackClickListener = View.OnClickListener {
        val contact = if (conversation == null) null else conversation!!.contact
        if (contact != null) {
            chatsActivity!!.xmppConnectionService.createContact(contact, true)
            chatsActivity!!.switchToContactDetails(contact)
        }
    }
    private val mLongPressBlockListener = OnLongClickListener { view: View -> showBlockSubmenu(view) }
    private val mAllowPresenceSubscription = View.OnClickListener {
        val contact = if (conversation == null) null else conversation!!.contact
        if (contact != null) {
            chatsActivity!!.xmppConnectionService.sendPresencePacket(
                contact.account,
                chatsActivity!!.xmppConnectionService
                    .presenceGenerator
                    .sendPresenceUpdatesTo(contact)
            )
            hideSnackbar()
        }
    }
    private var clickToDecryptListener = View.OnClickListener {
        val pendingIntent = conversation!!.account.pgpDecryptionService.pendingIntent
        if (pendingIntent != null) {
            try {
                requireActivity().startIntentSenderForResult(
                    pendingIntent.intentSender,
                    REQUEST_DECRYPT_PGP,
                    null,
                    0,
                    0,
                    0
                )
            } catch (e: SendIntentException) {
                Toast.makeText(
                    activity,
                    R.string.unable_to_connect_to_keychain,
                    Toast.LENGTH_SHORT
                )
                    .show()
                conversation
                    ?.account
                    ?.pgpDecryptionService
                    ?.continueDecryption(true)
            }
        }
        updateSnackBar(conversation)
    }

    private val sendingPgpMessage = AtomicBoolean(false)

    private val editorActionListener = OnEditorActionListener { v: TextView, actionId: Int, _: KeyEvent? ->
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            val imm = chatsActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            if (imm != null && imm.isFullscreenMode) {
                imm.hideSoftInputFromWindow(v.windowToken, 0)
            }
            sendMessage()
            true
        } else {
            false
        }
    }

    private val scrollButtonListener = View.OnClickListener {
        stopScrolling()
        setSelection(binding.rvMessagesList.count - 1, true)
    }

    private val sendButtonListener = View.OnClickListener { v ->
        val tag = v.tag
        if (tag is SendButtonAction) {
            when (tag) {
                SendButtonAction.TAKE_PHOTO, SendButtonAction.RECORD_VIDEO, SendButtonAction.SEND_LOCATION, SendButtonAction.RECORD_VOICE, SendButtonAction.CHOOSE_PICTURE -> attachFile(
                    tag.toChoice()
                )
                SendButtonAction.CANCEL -> if (conversation != null) {
                    if (conversation!!.setCorrectingMessage(null)) {
                        binding.editMessageInput.setText("")
                        binding.editMessageInput.append(conversation!!.draftMessage)
                        conversation?.draftMessage = null
                    } else if (conversation!!.mode == Conversation.MODE_MULTI) {
                        conversation?.nextCounterpart = null
                        binding.editMessageInput.setText("")
                    } else {
                        binding.editMessageInput.setText("")
                    }
                    updateSendButton()
                    updateEditablity()
                }
                else -> sendMessage()
            }
        } else {
            sendMessage()
        }
    }

    private fun toggleScrollDownButton(listView: AbsListView = binding.rvMessagesList) {
        if (conversation == null) {
            return
        }
        if (scrolledToBottom(listView)) {
            lastMessageUuid = null
            hideUnreadMessagesCount()
        } else {
            binding.scrollToBottomButton.isEnabled = true
            binding.scrollToBottomButton.show()
            if (lastMessageUuid == null) {
                lastMessageUuid = conversation!!.latestMessage.uuid
            }
        }
    }

    private fun getIndexOf(uuid: String?, messages: List<Message>): Int {
        if (uuid == null) {
            return messages.size - 1
        }
        for (i in messages.indices) {
            if (uuid == messages[i].uuid) {
                return i
            } else {
                var next: Message? = messages[i]
                while (next != null && next.wasMergedIntoPrevious()) {
                    if (uuid == next.uuid) {
                        return i
                    }
                    next = next.next()
                }
            }
        }
        return -1
    }

    private val scrollPosition: ScrollState?
        get() {
            val listView = if (!isBindingNotNull) null else binding.rvMessagesList
            return if (listView == null || listView.count == 0 || listView.lastVisiblePosition == listView.count - 1) {
                null
            } else {
                val pos = listView.firstVisiblePosition
                val view = listView.getChildAt(0)
                if (view == null) {
                    null
                } else {
                    ScrollState(pos, view.top)
                }
            }
        }

    private fun setScrollPosition(scrollPosition: ScrollState?, lastMessageUuid: String?) {
        if (scrollPosition != null) {
            this.lastMessageUuid = lastMessageUuid
            binding.rvMessagesList.setSelectionFromTop(scrollPosition.position, scrollPosition.offset)
            toggleScrollDownButton()
        }
    }

    private fun attachLocationToConversation(conversation: Conversation?, uri: Uri) {
        if (conversation == null) {
            return
        }
        chatsActivity?.xmppConnectionService?.attachLocationToConversation(
            conversation,
            uri,
            object : UiCallback<Message?> {
                override fun success(message: Message?) {}
                override fun error(errorCode: Int, `object`: Message?) {
                    // TODO show possible pgp error
                }

                override fun userInputRequired(pi: PendingIntent, `object`: Message?) {}
            })
    }

    private fun attachFileToConversation(conversation: Conversation?, uri: Uri, type: String) {
        if (conversation == null) {
            return
        }
        val prepareFileToast = Toast.makeText(activity, getText(R.string.preparing_file), Toast.LENGTH_LONG)
        prepareFileToast.show()
        chatsActivity!!.delegateUriPermissionsToService(uri)
        chatsActivity!!.xmppConnectionService.attachFileToConversation(
            conversation,
            uri,
            type,
            object : UiInformableCallback<Message?> {
                override fun inform(text: String) {
                    hidePrepareFileToast(prepareFileToast)
                    runOnUiThread { chatsActivity!!.replaceToast(text) }
                }

                override fun success(message: Message?) {
                    runOnUiThread { chatsActivity!!.hideToast() }
                    hidePrepareFileToast(prepareFileToast)
                }

                override fun error(errorCode: Int, message: Message?) {
                    hidePrepareFileToast(prepareFileToast)
                    runOnUiThread { chatsActivity!!.replaceToast(getString(errorCode)) }
                }

                override fun userInputRequired(pi: PendingIntent, message: Message?) {
                    hidePrepareFileToast(prepareFileToast)
                }
            })
    }

    private fun attachEditorContentToConversation(uri: Uri?) {
        mediaPreviewAdapter?.addMediaPreviews(Attachment.of(activity, uri, Attachment.Type.FILE))
        toggleInputMethod()
    }

    private fun attachImageToConversation(conversation: Conversation?, uri: Uri, type: String) {
        if (conversation == null) {
            return
        }
        val prepareFileToast = Toast.makeText(activity, getText(R.string.preparing_image), Toast.LENGTH_LONG)
        prepareFileToast.show()
        chatsActivity!!.delegateUriPermissionsToService(uri)
        chatsActivity!!.xmppConnectionService.attachImageToConversation(
            conversation,
            uri,
            type,
            object : UiCallback<Message?> {
                override fun userInputRequired(pi: PendingIntent, `object`: Message?) {
                    hidePrepareFileToast(prepareFileToast)
                }

                override fun success(message: Message?) {
                    hidePrepareFileToast(prepareFileToast)
                }

                override fun error(error: Int, message: Message?) {
                    hidePrepareFileToast(prepareFileToast)
                    val activity = chatsActivity ?: return
                    activity.runOnUiThread { activity.replaceToast(getString(error)) }
                }
            })
    }

    private fun hidePrepareFileToast(prepareFileToast: Toast?) {
        if (prepareFileToast != null && chatsActivity != null) {
            chatsActivity!!.runOnUiThread { prepareFileToast.cancel() }
        }
    }

    private fun sendMessage() {
        if (mediaPreviewAdapter!!.hasAttachments()) {
            commitAttachments()
            return
        }
        val text = binding.editMessageInput.text
        val body = text?.toString() ?: ""
        val conversation = conversation
        if (body.isEmpty() || conversation == null) {
            return
        }
        if (trustKeysIfNeeded(conversation, REQUEST_TRUST_KEYS_TEXT)) {
            return
        }
        val message: Message
        if (conversation.correctingMessage == null) {
            message = Message(conversation, body, conversation.nextEncryption)
            Message.configurePrivateMessage(message)
        } else {
            message = conversation.correctingMessage
            message.body = body
            message.putEdited(message.uuid, message.serverMsgId)
            message.serverMsgId = null
            message.uuid = UUID.randomUUID().toString()
        }
        when (conversation.nextEncryption) {
            Message.ENCRYPTION_PGP -> sendPgpMessage(message)
            else -> sendMessage(message)
        }
    }

    private fun trustKeysIfNeeded(conversation: Conversation?, requestCode: Int): Boolean {
        return (conversation!!.nextEncryption == Message.ENCRYPTION_AXOLOTL
            && trustKeysIfNeeded(requestCode))
    }

    private fun trustKeysIfNeeded(requestCode: Int): Boolean {
        val axolotlService = conversation!!.account.axolotlService
        val targets = axolotlService.getCryptoTargets(conversation)
        val hasUnaccepted = !conversation!!.acceptedCryptoTargets.containsAll(targets)
        val hasUndecidedOwn = axolotlService
            .getKeysWithTrust(FingerprintStatus.createActiveUndecided()).isNotEmpty()
        val hasUndecidedContacts = axolotlService
            .getKeysWithTrust(FingerprintStatus.createActiveUndecided(), targets).isNotEmpty()
        val hasPendingKeys = axolotlService.findDevicesWithoutSession(conversation).isNotEmpty()
        val hasNoTrustedKeys = axolotlService.anyTargetHasNoTrustedKeys(targets)
        val downloadInProgress = axolotlService.hasPendingKeyFetches(targets)
        return if (hasUndecidedOwn
            || hasUndecidedContacts
            || hasPendingKeys
            || hasNoTrustedKeys
            || hasUnaccepted
            || downloadInProgress
        ) {
            axolotlService.createSessionsIfNeeded(conversation)
            val intent = Intent(activity, TrustKeysActivity::class.java)
            val contacts = arrayOfNulls<String>(targets.size)
            for (i in contacts.indices) {
                contacts[i] = targets[i].toString()
            }
            intent.putExtra("contacts", contacts)
            intent.putExtra(
                XmppActivity.EXTRA_ACCOUNT,
                conversation!!.account.jid.asBareJid().toEscapedString()
            )
            intent.putExtra("conversation", conversation!!.uuid)
            startActivityForResult(intent, requestCode)
            true
        } else {
            false
        }
    }

    private fun setupIme() {
        binding.editMessageInput.refreshIme()
    }

    private fun handleActivityResult(activityResult: ActivityResult) {
        if (activityResult.resultCode == Activity.RESULT_OK) {
            handlePositiveActivityResult(activityResult.requestCode, activityResult.data)
        } else {
            handleNegativeActivityResult(activityResult.requestCode)
        }
    }

    private fun handlePositiveActivityResult(requestCode: Int, data: Intent) {
        when (requestCode) {
            REQUEST_TRUST_KEYS_TEXT -> sendMessage()
            REQUEST_TRUST_KEYS_ATTACHMENTS -> commitAttachments()
            REQUEST_START_AUDIO_CALL -> triggerRtpSession(RtpSessionActivity.ACTION_MAKE_VOICE_CALL)
            REQUEST_START_VIDEO_CALL -> triggerRtpSession(RtpSessionActivity.ACTION_MAKE_VIDEO_CALL)
            ATTACHMENT_CHOICE_CHOOSE_IMAGE -> {
                val imageUris = Attachment.extractAttachments(activity, data, Attachment.Type.IMAGE)
                mediaPreviewAdapter!!.addMediaPreviews(imageUris)
                toggleInputMethod()
            }
            ATTACHMENT_CHOICE_TAKE_PHOTO -> {
                val takePhotoUri = pendingTakePhotoUri.pop()
                if (takePhotoUri != null) {
                    mediaPreviewAdapter!!.addMediaPreviews(
                        Attachment.of(activity, takePhotoUri, Attachment.Type.IMAGE)
                    )
                    toggleInputMethod()
                } else {
                    Timber.d("lost take photo uri. unable to to attach")
                }
            }
            ATTACHMENT_CHOICE_CHOOSE_FILE, ATTACHMENT_CHOICE_RECORD_VIDEO, ATTACHMENT_CHOICE_RECORD_VOICE -> {
                val type =
                    if (requestCode == ATTACHMENT_CHOICE_RECORD_VOICE) Attachment.Type.RECORDING else Attachment.Type.FILE
                val fileUris = Attachment.extractAttachments(activity, data, type)
                mediaPreviewAdapter!!.addMediaPreviews(fileUris)
                toggleInputMethod()
            }
            ATTACHMENT_CHOICE_LOCATION -> {
                val latitude = data.getDoubleExtra("latitude", 0.0)
                val longitude = data.getDoubleExtra("longitude", 0.0)
                val accuracy = data.getIntExtra("accuracy", 0)
                val geo: Uri = if (accuracy > 0) {
                    Uri.parse(String.format("geo:%s,%s;u=%s", latitude, longitude, accuracy))
                } else {
                    Uri.parse(String.format("geo:%s,%s", latitude, longitude))
                }
                mediaPreviewAdapter!!.addMediaPreviews(
                    Attachment.of(activity, geo, Attachment.Type.LOCATION)
                )
                toggleInputMethod()
            }
            XmppActivity.REQUEST_INVITE_TO_CONVERSATION -> {
                val invite = ConferenceInvite.parse(data)
                if (invite != null) {
                    if (invite.execute(chatsActivity)) {
                        chatsActivity!!.mToast = Toast.makeText(
                            chatsActivity, R.string.creating_conference, Toast.LENGTH_LONG
                        )
                        chatsActivity!!.mToast.show()
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun commitAttachments() {
        val attachments: MutableList<Attachment> = mediaPreviewAdapter!!.attachments
        if (anyNeedsExternalStoragePermission(attachments)
            && !hasPermissions(
                REQUEST_COMMIT_ATTACHMENTS, Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            return
        }
        if (trustKeysIfNeeded(conversation, REQUEST_TRUST_KEYS_ATTACHMENTS)) {
            return
        }
        val callback = OnPresenceSelected {
            val i = attachments.iterator()
            while (i.hasNext()) {
                val attachment = i.next()
                when (attachment.type) {
                    Attachment.Type.LOCATION -> {
                        attachLocationToConversation(conversation, attachment.uri)
                    }
                    Attachment.Type.IMAGE -> {
                        Timber.d("ConversationsActivity.commitAttachments() - attaching image to conversations. CHOOSE_IMAGE")
                        attachImageToConversation(
                            conversation, attachment.uri, attachment.mime
                        )
                    }
                    else -> {
                        Timber.d("ConversationsActivity.commitAttachments() - attaching file to conversations. CHOOSE_FILE/RECORD_VOICE/RECORD_VIDEO")
                        attachFileToConversation(
                            conversation, attachment.uri, attachment.mime
                        )
                    }
                }
                i.remove()
            }
            mediaPreviewAdapter?.notifyDataSetChanged()
            toggleInputMethod()
        }
        if (conversation == null || conversation!!.mode == Conversation.MODE_MULTI || Attachment.canBeSendInband(
                attachments
            )
            || (conversation!!.account.httpUploadAvailable()
                && FileBackend.allFilesUnderSize(
                activity, attachments, getMaxHttpUploadSize(conversation!!)
            ))
        ) {
            callback.onPresenceSelected()
        } else {
            chatsActivity!!.selectPresence(conversation, callback)
        }
    }

    fun toggleInputMethod() = with(binding) {
        val hasAttachments = mediaPreviewAdapter!!.hasAttachments()
        editMessageInput.visibility = if (hasAttachments) View.GONE else View.VISIBLE
        imageAttach.visibility = if (hasAttachments) View.GONE else View.VISIBLE
        mediaPreview.isVisible = hasAttachments
        updateSendButton()
    }

    private fun handleNegativeActivityResult(requestCode: Int) {
        when (requestCode) {
            ATTACHMENT_CHOICE_TAKE_PHOTO -> if (pendingTakePhotoUri.clear()) {
                Timber.d("cleared pending photo uri after negative activity result")
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val activityResult = ActivityResult.of(requestCode, resultCode, data)
        if (chatsActivity != null && chatsActivity!!.xmppConnectionService != null) {
            handleActivityResult(activityResult)
        } else {
            postponedActivityResult.push(activityResult)
        }
    }

    private fun unblockConversation(conversation: Blockable?) {
        chatsActivity!!.xmppConnectionService.sendUnblockRequest(conversation)
    }

    @Suppress("DEPRECATION")
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        Timber.d("ConversationFragment.onAttach()")
        if (activity is ChatsActivity) {
            chatsActivity = activity
        } else {
            throw IllegalStateException(
                "Trying to attach fragment to activity that is not the ConversationsActivity"
            )
        }
    }

    override fun onDetach() {
        super.onDetach()
        chatsActivity = null // TODO maybe not a good idea since some callbacks really need it
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        root.setOnClickListener(null) // TODO why the fuck did we do this?
        textRecipientName.text = conversation?.name
        imageBack.setOnClickListener { requireActivity().onBackPressed() }

        editMessageInput.apply {
            addTextChangedListener(MessageEditorStyler(this))
            setOnEditorActionListener(editorActionListener)
            setRichContentListener(arrayOf("image/*"), editorContentListener)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                customInsertionActionModeCallback = EditMessageActionModeCallback(this)
            }
        }

        imageSend.setOnClickListener(sendButtonListener)
        imageAttach.setOnClickListener { showAttachmentsDialog() }

        messageListAdapter = MessageAdapter(requireActivity() as XmppActivity, messageList)
        messageListAdapter?.setOnContactPictureClicked(this@ChatFragment)
        messageListAdapter?.setOnContactPictureLongClicked(this@ChatFragment)

        rvMessagesList.apply {
            setOnScrollListener(onScrollListener)
            transcriptMode = ListView.TRANSCRIPT_MODE_NORMAL
            adapter = messageListAdapter
        }
        registerForContextMenu(rvMessagesList)

        mediaPreviewAdapter = MediaPreviewAdapter(this@ChatFragment)
        mediaPreview.adapter = mediaPreviewAdapter

        checkForCallsAvailability()
        imageVideoCall.setOnClickListener { checkPermissionAndTriggerVideoCall() }
        imageCall.setOnClickListener { checkPermissionAndTriggerAudioCall() }
        binding.scrollToBottomButton.setOnClickListener(scrollButtonListener)
        setupClickListener(imageMore)
        initObservers()
    }

    override fun onClick(v: View?) {
        viewModel.onClick(v)
    }

    override fun onMoreDialogShow(ignore: Unit) = with(binding) {
        val popupMenu = PopupMenu(requireContext(), imageMore)
        popupMenu.inflate(R.menu.chat_menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_search -> {
                    viewModel.onSearchClick(conversation?.uuid)
                    true
                }
                else -> {
                    false
                }
            }
        }
        popupMenu.show()
    }

    override fun onVisibilityLoader(isVisibleLoader: Boolean) = Unit

    private fun initObservers() = with(viewModel) {
        observe(onMoreDialogShow, ::onMoreDialogShow)
    }

    private fun checkForCallsAvailability() = with(binding) {
        val rtpCapability = RtpCapability.check(conversation?.contact)
        val cameraAvailable = chatsActivity != null && chatsActivity?.isCameraFeatureAvailable == true
        imageCall.isVisible = (rtpCapability != RtpCapability.Capability.NONE)
        imageVideoCall.isVisible = (rtpCapability == RtpCapability.Capability.VIDEO && cameraAvailable)
    }

    private fun showAttachmentsDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), com.topiichat.core.R.style.BottomSheetDialog)
        val dialogBinding = DialogAddAttachmentBinding.inflate(layoutInflater, null, false)
        with(dialogBinding) {
            layoutCamera.setOnClickListener {
                attachFile(ATTACHMENT_CHOICE_TAKE_PHOTO)
                bottomSheetDialog.dismiss()
            }
            layoutGallery.setOnClickListener {
                attachFile(ATTACHMENT_CHOICE_CHOOSE_IMAGE)
                bottomSheetDialog.dismiss()
            }
            layoutLocation.setOnClickListener {
                attachFile(ATTACHMENT_CHOICE_LOCATION)
                bottomSheetDialog.dismiss()
            }
            layoutDocument.setOnClickListener {
                attachFile(ATTACHMENT_CHOICE_CHOOSE_FILE)
                bottomSheetDialog.dismiss()
            }
            layoutContact.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            textClose.setOnClickListener { bottomSheetDialog.dismiss() }
        }
        bottomSheetDialog.setContentView(dialogBinding.root)
        bottomSheetDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        bottomSheetDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.d("ConversationFragment.onDestroyView()")
        messageListAdapter!!.setOnContactPictureClicked(null)
        messageListAdapter!!.setOnContactPictureLongClicked(null)
    }

    private fun quoteText(text: String) {
        if (binding.editMessageInput.isEnabled) {
            binding.editMessageInput.insertAsQuote(text);
            binding.editMessageInput.requestFocus()
            val inputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            inputMethodManager?.showSoftInput(
                binding.editMessageInput, InputMethodManager.SHOW_IMPLICIT
            )
        }
    }

    private fun quoteMessage(message: Message?) {
        quoteText(MessageUtils.prepareQuote(message))
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo?) {
        // This should cancel any remaining click events that would otherwise trigger links
        v.dispatchTouchEvent(MotionEvent.obtain(0, 0, MotionEvent.ACTION_CANCEL, 0f, 0f, 0))
        synchronized(messageList) {
            super.onCreateContextMenu(menu, v, menuInfo)
            val acmi = menuInfo as AdapterContextMenuInfo?
            selectedMessage = messageList[acmi!!.position]
            populateContextMenu(menu)
        }
    }

    private fun populateContextMenu(menu: ContextMenu) {
        val m = selectedMessage
        val t = m!!.transferable
        var relevantForCorrection = m
        while (relevantForCorrection!!.mergeable(relevantForCorrection.next())) {
            relevantForCorrection = relevantForCorrection.next()
        }
        if (m.type != Message.TYPE_STATUS && m.type != Message.TYPE_RTP_SESSION) {
            if (m.encryption == Message.ENCRYPTION_AXOLOTL_NOT_FOR_THIS_DEVICE
                || m.encryption == Message.ENCRYPTION_AXOLOTL_FAILED
            ) {
                return
            }
            if (m.status == Message.STATUS_RECEIVED && t != null && (t.status == Transferable.STATUS_CANCELLED
                    || t.status == Transferable.STATUS_FAILED)
            ) {
                return
            }
            val deleted = m.isDeleted
            val encrypted = (m.encryption == Message.ENCRYPTION_DECRYPTION_FAILED
                || m.encryption == Message.ENCRYPTION_PGP)
            val receiving = (m.status == Message.STATUS_RECEIVED
                && (t is JingleFileTransferConnection
                || t is HttpDownloadConnection))
            chatsActivity!!.menuInflater.inflate(R.menu.message_context, menu)
            menu.setHeaderTitle(R.string.message_options)
            val openWith = menu.findItem(R.id.open_with)
            val copyMessage = menu.findItem(R.id.copy_message)
            val copyLink = menu.findItem(R.id.copy_link)
            val quoteMessage = menu.findItem(R.id.quote_message)
            val retryDecryption = menu.findItem(R.id.retry_decryption)
            val correctMessage = menu.findItem(R.id.correct_message)
            val shareWith = menu.findItem(R.id.share_with)
            val sendAgain = menu.findItem(R.id.send_again)
            val copyUrl = menu.findItem(R.id.copy_url)
            val downloadFile = menu.findItem(R.id.download_file)
            val cancelTransmission = menu.findItem(R.id.cancel_transmission)
            val deleteFile = menu.findItem(R.id.delete_file)
            val showErrorMessage = menu.findItem(R.id.show_error_message)
            val unInitiatedButKnownSize = MessageUtils.unInitiatedButKnownSize(m)
            val showError =
                m.status == Message.STATUS_SEND_FAILED && m.errorMessage != null && Message.ERROR_MESSAGE_CANCELLED != m.errorMessage
            if (!m.isFileOrImage
                && !encrypted
                && !m.isGeoUri
                && !m.treatAsDownloadable()
                && !unInitiatedButKnownSize
                && t == null
            ) {
                copyMessage.isVisible = true
                quoteMessage.isVisible = !showError && MessageUtils.prepareQuote(m).length > 0
                val scheme = ShareUtil.getLinkScheme(m.mergedBody)
                if ("xmpp" == scheme) {
                    copyLink.setTitle(R.string.copy_jabber_id)
                    copyLink.isVisible = true
                } else if (scheme != null) {
                    copyLink.isVisible = true
                }
            }
            if (m.encryption == Message.ENCRYPTION_DECRYPTION_FAILED && !deleted) {
                retryDecryption.isVisible = true
            }
            if (!showError
                && relevantForCorrection.type == Message.TYPE_TEXT && !m.isGeoUri
                && relevantForCorrection.isLastCorrectableMessage
                && m.conversation is Conversation
            ) {
                correctMessage.isVisible = true
            }
            if (m.isFileOrImage && !deleted && !receiving
                || (m.type == Message.TYPE_TEXT && !m.treatAsDownloadable()
                    && !unInitiatedButKnownSize
                    && t == null)
            ) {
                shareWith.isVisible = true
            }
            if (m.status == Message.STATUS_SEND_FAILED) {
                sendAgain.isVisible = true
            }
            if (m.hasFileOnRemoteHost()
                || m.isGeoUri
                || m.treatAsDownloadable()
                || unInitiatedButKnownSize
                || t is HttpDownloadConnection
            ) {
                copyUrl.isVisible = true
            }
            if (m.isFileOrImage && deleted && m.hasFileOnRemoteHost()) {
                downloadFile.isVisible = true
                downloadFile.title = chatsActivity!!.getString(
                    R.string.download_x_file,
                    UIHelper.getFileDescriptionString(chatsActivity, m)
                )
            }
            val waitingOfferedSending =
                m.status == Message.STATUS_WAITING || m.status == Message.STATUS_UNSEND || m.status == Message.STATUS_OFFERED
            val cancelable = t != null && !deleted || waitingOfferedSending && m.needsUploading()
            if (cancelable) {
                cancelTransmission.isVisible = true
            }
            if (m.isFileOrImage && !deleted && !cancelable) {
                val path = m.relativeFilePath
                if (path == null || !path.startsWith("/")
                    || FileBackend.inConversationsDirectory(requireActivity(), path)
                ) {
                    deleteFile.isVisible = true
                    deleteFile.title = chatsActivity!!.getString(
                        R.string.delete_x_file,
                        UIHelper.getFileDescriptionString(chatsActivity, m)
                    )
                }
            }
            if (showError) {
                showErrorMessage.isVisible = true
            }
            val mime = if (m.isFileOrImage) m.mimeType else null
            if (m.isGeoUri && GeoHelper.openInOsmAnd(activity, m)
                || mime != null && mime.startsWith("audio/")
            ) {
                openWith.isVisible = true
            }
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == R.id.share_with) {
            ShareUtil.share(chatsActivity, selectedMessage)
            return true
        } else if (itemId == R.id.correct_message) {
            correctMessage(selectedMessage)
            return true
        } else if (itemId == R.id.copy_message) {
            ShareUtil.copyToClipboard(chatsActivity, selectedMessage)
            return true
        } else if (itemId == R.id.copy_link) {
            ShareUtil.copyLinkToClipboard(chatsActivity, selectedMessage)
            return true
        } else if (itemId == R.id.quote_message) {
            quoteMessage(selectedMessage)
            return true
        } else if (itemId == R.id.send_again) {
            resendMessage(selectedMessage)
            return true
        } else if (itemId == R.id.copy_url) {
            ShareUtil.copyUrlToClipboard(chatsActivity, selectedMessage)
            return true
        } else if (itemId == R.id.download_file) {
            startDownloadable(selectedMessage)
            return true
        } else if (itemId == R.id.cancel_transmission) {
            cancelTransmission(selectedMessage)
            return true
        } else if (itemId == R.id.retry_decryption) {
            retryDecryption(selectedMessage)
            return true
        } else if (itemId == R.id.delete_file) {
            deleteFile(selectedMessage)
            return true
        } else if (itemId == R.id.show_error_message) {
            showErrorMessage(selectedMessage)
            return true
        } else if (itemId == R.id.open_with) {
            openWith(selectedMessage)
            return true
        }
        return super.onContextItemSelected(item)
    }

    private fun returnToOngoingCall() {
        val ongoingRtpSession = chatsActivity!!.xmppConnectionService
            .jingleConnectionManager
            .getOngoingRtpConnection(conversation!!.contact)
        if (ongoingRtpSession.isPresent) {
            val id = ongoingRtpSession.get()
            val intent = Intent(activity, RtpSessionActivity::class.java)
            intent.putExtra(
                RtpSessionActivity.EXTRA_ACCOUNT,
                id.account.jid.asBareJid().toEscapedString()
            )
            intent.putExtra(RtpSessionActivity.EXTRA_WITH, id.with.toEscapedString())
            if (id is Id) {
                intent.action = Intent.ACTION_VIEW
                intent.putExtra(RtpSessionActivity.EXTRA_SESSION_ID, id.getSessionId())
            } else if (id is RtpSessionProposal) {
                if (id.media.contains(Media.VIDEO)) {
                    intent.action = RtpSessionActivity.ACTION_MAKE_VIDEO_CALL
                } else {
                    intent.action = RtpSessionActivity.ACTION_MAKE_VOICE_CALL
                }
            }
            startActivity(intent)
        }
    }

    private fun checkPermissionAndTriggerAudioCall() {
        if (chatsActivity!!.mUseTor || conversation!!.account.isOnion) {
            Toast.makeText(chatsActivity, R.string.disable_tor_to_make_call, Toast.LENGTH_SHORT).show()
            return
        }
        val permissions: List<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        } else {
            listOf(Manifest.permission.RECORD_AUDIO)
        }
        if (hasPermissions(REQUEST_START_AUDIO_CALL, permissions)) {
            triggerRtpSession(RtpSessionActivity.ACTION_MAKE_VOICE_CALL)
        }
    }

    private fun checkPermissionAndTriggerVideoCall() {
        if (chatsActivity!!.mUseTor || conversation!!.account.isOnion) {
            Toast.makeText(chatsActivity, R.string.disable_tor_to_make_call, Toast.LENGTH_SHORT).show()
            return
        }
        val permissions: List<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        } else {
            listOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA
            )
        }
        if (hasPermissions(REQUEST_START_VIDEO_CALL, permissions)) {
            triggerRtpSession(RtpSessionActivity.ACTION_MAKE_VIDEO_CALL)
        }
    }

    private fun triggerRtpSession(action: String) {
        if (chatsActivity!!.xmppConnectionService.jingleConnectionManager.isBusy) {
            Toast.makeText(activity, R.string.only_one_call_at_a_time, Toast.LENGTH_LONG)
                .show()
            return
        }
        conversation?.contact?.let { contact ->
            if (contact.presences.anySupport(Namespace.JINGLE_MESSAGE)) {
                triggerRtpSession(contact.account, contact.jid.asBareJid(), action)
            } else {
                val capability: RtpCapability.Capability = if (action == RtpSessionActivity.ACTION_MAKE_VIDEO_CALL) {
                    RtpCapability.Capability.VIDEO
                } else {
                    RtpCapability.Capability.AUDIO
                }
                PresenceSelector.selectFullJidForDirectRtpConnection(
                    chatsActivity,
                    contact,
                    capability
                ) { fullJid: Jid -> triggerRtpSession(contact.account, fullJid, action) }
            }
        }

    }

    private fun triggerRtpSession(account: Account, with: Jid, action: String) {
        val intent = Intent(chatsActivity, RtpSessionActivity::class.java)
        intent.action = action
        intent.putExtra(RtpSessionActivity.EXTRA_ACCOUNT, account.jid.toEscapedString())
        intent.putExtra(RtpSessionActivity.EXTRA_WITH, with.toEscapedString())
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    @JvmOverloads fun attachFile(attachmentChoice: Int, updateRecentlyUsed: Boolean = true) {
        if (attachmentChoice == ATTACHMENT_CHOICE_RECORD_VOICE) {
            if (!hasPermissions(
                    attachmentChoice,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO
                )
            ) {
                return
            }
        } else if (attachmentChoice == ATTACHMENT_CHOICE_TAKE_PHOTO
            || attachmentChoice == ATTACHMENT_CHOICE_RECORD_VIDEO
        ) {
            if (!hasPermissions(
                    attachmentChoice,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                )
            ) {
                return
            }
        } else if (attachmentChoice != ATTACHMENT_CHOICE_LOCATION) {
            if (!hasPermissions(attachmentChoice, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                return
            }
        }
        if (updateRecentlyUsed) {
            storeRecentlyUsedQuickAction(attachmentChoice)
        }
        val encryption = conversation!!.nextEncryption
        val mode = conversation!!.mode
        if (encryption == Message.ENCRYPTION_PGP) {
            if (chatsActivity!!.hasPgp()) {
                if (mode == Conversation.MODE_SINGLE
                    && conversation!!.contact.pgpKeyId != 0L
                ) {
                    chatsActivity!!.xmppConnectionService
                        .pgpEngine
                        .hasKey(
                            conversation!!.contact,
                            object : UiCallback<Contact?> {
                                override fun userInputRequired(
                                    pi: PendingIntent, contact: Contact?
                                ) {
                                    startPendingIntent(pi, attachmentChoice)
                                }

                                override fun success(contact: Contact?) {
                                    invokeAttachFileIntent(attachmentChoice)
                                }

                                override fun error(error: Int, contact: Contact?) {
                                    chatsActivity!!.replaceToast(getString(error))
                                }
                            })
                } else if (mode == Conversation.MODE_MULTI
                    && conversation!!.mucOptions.pgpKeysInUse()
                ) {
                    if (!conversation!!.mucOptions.everybodyHasKeys()) {
                        val warning = Toast.makeText(
                            activity,
                            R.string.missing_public_keys,
                            Toast.LENGTH_LONG
                        )
                        warning.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                        warning.show()
                    }
                    invokeAttachFileIntent(attachmentChoice)
                } else {
                    showNoPGPKeyDialog(
                        false
                    ) { dialog: DialogInterface?, which: Int ->
                        conversation!!.nextEncryption = Message.ENCRYPTION_NONE
                        chatsActivity!!.xmppConnectionService.updateConversation(conversation)
                        invokeAttachFileIntent(attachmentChoice)
                    }
                }
            } else {
                chatsActivity!!.showInstallPgpDialog()
            }
        } else {
            invokeAttachFileIntent(attachmentChoice)
        }
    }

    private fun storeRecentlyUsedQuickAction(attachmentChoice: Int) {
        try {
            chatsActivity!!.preferences
                .edit()
                .putString(
                    RECENTLY_USED_QUICK_ACTION,
                    SendButtonAction.of(attachmentChoice).toString()
                )
                .apply()
        } catch (e: IllegalArgumentException) {
            // just do not save
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        val permissionResult = PermissionUtils.removeBluetoothConnect(permissions, grantResults)
        if (grantResults.size > 0) {
            if (PermissionUtils.allGranted(permissionResult.grantResults)) {
                when (requestCode) {
                    REQUEST_START_DOWNLOAD -> if (pendingDownloadableMessage != null) {
                        startDownloadable(pendingDownloadableMessage)
                    }
                    REQUEST_ADD_EDITOR_CONTENT -> if (mPendingEditorContent != null) {
                        attachEditorContentToConversation(mPendingEditorContent)
                    }
                    REQUEST_COMMIT_ATTACHMENTS -> commitAttachments()
                    REQUEST_START_AUDIO_CALL -> triggerRtpSession(RtpSessionActivity.ACTION_MAKE_VOICE_CALL)
                    REQUEST_START_VIDEO_CALL -> triggerRtpSession(RtpSessionActivity.ACTION_MAKE_VIDEO_CALL)
                    else -> attachFile(requestCode)
                }
            } else {
                @StringRes val res: Int
                val firstDenied =
                    PermissionUtils.getFirstDenied(permissionResult.grantResults, permissionResult.permissions)
                res = if (Manifest.permission.RECORD_AUDIO == firstDenied) {
                    R.string.no_microphone_permission
                } else if (Manifest.permission.CAMERA == firstDenied) {
                    R.string.no_camera_permission
                } else {
                    R.string.no_storage_permission
                }
            }
        }
        if (PermissionUtils.writeGranted(grantResults, permissions)) {
            if (chatsActivity != null && chatsActivity!!.xmppConnectionService != null) {
                chatsActivity!!.xmppConnectionService.bitmapCache.evictAll()
                chatsActivity!!.xmppConnectionService.restartFileObserver()
            }
            refresh()
        }
    }

    fun startDownloadable(message: Message?) {
        if (!hasPermissions(REQUEST_START_DOWNLOAD, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            pendingDownloadableMessage = message
            return
        }
        val transferable = message!!.transferable
        if (transferable != null) {
            if (transferable is TransferablePlaceholder && message.hasFileOnRemoteHost()) {
                createNewConnection(message)
                return
            }
            if (!transferable.start()) {
                Timber.d("type: " + transferable.javaClass.name)
                Toast.makeText(activity, R.string.not_connected_try_again, Toast.LENGTH_SHORT)
                    .show()
            }
        } else if (message.treatAsDownloadable()
            || message.hasFileOnRemoteHost()
            || MessageUtils.unInitiatedButKnownSize(message)
        ) {
            createNewConnection(message)
        } else {
            Timber.d(message.conversation.account.toString() + ": unable to start downloadable")
        }
    }

    private fun createNewConnection(message: Message?) {
        if (!chatsActivity!!.xmppConnectionService.hasInternetConnection()) {
            Toast.makeText(activity, R.string.not_connected_try_again, Toast.LENGTH_SHORT)
                .show()
            return
        }
        chatsActivity!!.xmppConnectionService
            .httpConnectionManager
            .createNewDownloadConnection(message, true)
    }

    @SuppressLint("InflateParams") protected fun clearHistoryDialog(conversation: Conversation?) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.clear_conversation_history))
        val dialogView = requireActivity().layoutInflater.inflate(R.layout.dialog_clear_history, null)
        val endConversationCheckBox = dialogView.findViewById<CheckBox>(R.id.end_conversation_checkbox)
        builder.setView(dialogView)
        builder.setNegativeButton(getString(R.string.cancel), null)
        builder.setPositiveButton(
            getString(R.string.confirm)
        ) { dialog: DialogInterface?, which: Int ->
            chatsActivity!!.xmppConnectionService.clearConversationHistory(conversation)
            if (endConversationCheckBox.isChecked) {
                chatsActivity!!.xmppConnectionService.archiveConversation(conversation)
                chatsActivity!!.onConversationArchived(conversation!!)
            } else {
                chatsActivity!!.onConversationsListItemUpdated()
                refresh()
            }
        }
        builder.create().show()
    }

    private fun hasPermissions(requestCode: Int, permissions: List<String>): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val missingPermissions: MutableList<String> = ArrayList()
            for (permission in permissions) {
                if (Config.ONLY_INTERNAL_STORAGE
                    && permission == Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) {
                    continue
                }
                if (chatsActivity!!.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    missingPermissions.add(permission)
                }
            }
            if (missingPermissions.size == 0) {
                true
            } else {
                requestPermissions(
                    missingPermissions.toTypedArray(),
                    requestCode
                )
                false
            }
        } else {
            true
        }
    }

    private fun hasPermissions(requestCode: Int, vararg permissions: String): Boolean {
        return hasPermissions(requestCode, ImmutableList.copyOf(permissions))
    }

    private fun invokeAttachFileIntent(attachmentChoice: Int) {
        var intent = Intent()
        var chooser = false
        when (attachmentChoice) {
            ATTACHMENT_CHOICE_CHOOSE_IMAGE -> {
                intent.action = Intent.ACTION_GET_CONTENT
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                intent.type = "image/*"
                chooser = true
            }
            ATTACHMENT_CHOICE_RECORD_VIDEO -> intent.action = MediaStore.ACTION_VIDEO_CAPTURE
            ATTACHMENT_CHOICE_TAKE_PHOTO -> {
                val uri = chatsActivity!!.xmppConnectionService.fileBackend.takePhotoUri
                pendingTakePhotoUri.push(uri)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.action = MediaStore.ACTION_IMAGE_CAPTURE
            }
            ATTACHMENT_CHOICE_CHOOSE_FILE -> {
                chooser = true
                intent.type = "*/*"
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.action = Intent.ACTION_GET_CONTENT
            }
            ATTACHMENT_CHOICE_RECORD_VOICE -> {}
            ATTACHMENT_CHOICE_LOCATION -> intent = GeoHelper.getFetchIntent(chatsActivity)
        }
        val context = activity ?: return
        try {
            if (chooser) {
                startActivityForResult(
                    Intent.createChooser(intent, getString(R.string.perform_action_with)),
                    attachmentChoice
                )
            } else {
                startActivityForResult(intent, attachmentChoice)
            }
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, R.string.no_application_found, Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.rvMessagesList.post { fireReadEvent() }
    }

    private fun fireReadEvent() {
        if (chatsActivity != null && conversation != null) {
            val uuid = lastVisibleMessageUuid
            if (uuid != null) {
                chatsActivity!!.onConversationRead(conversation!!, uuid)
            }
        }
    }

    // should not happen if we synchronize properly. however if that fails we
    // just gonna try item -1
    private val lastVisibleMessageUuid: String?
        get() {
            if (!isBindingNotNull) {
                return null
            }
            synchronized(messageList) {
                val pos = binding.rvMessagesList.lastVisiblePosition
                if (pos >= 0) {
                    var message: Message? = null
                    for (i in pos downTo 0) {
                        message = try {
                            binding.rvMessagesList.getItemAtPosition(i) as Message
                        } catch (e: IndexOutOfBoundsException) {
                            // should not happen if we synchronize properly. however if that fails we
                            // just gonna try item -1
                            continue
                        }
                        if (message!!.type != Message.TYPE_STATUS) {
                            break
                        }
                    }
                    if (message != null) {
                        while (message!!.next() != null && message.next().wasMergedIntoPrevious()) {
                            message = message.next()
                        }
                        return message.uuid
                    }
                }
            }
            return null
        }

    private fun openWith(message: Message?) {
        if (message?.isGeoUri == true) {
            GeoHelper.view(activity, message)
        } else {
            val file = chatsActivity?.xmppConnectionService?.fileBackend?.getFile(message)
            ViewUtil.view(chatsActivity, file)
        }
    }

    private fun showErrorMessage(message: Message?) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(R.string.error_message)
        val errorMessage = message!!.errorMessage
        val errorMessageParts: Array<String?> = errorMessage?.split("\\u001f")
            ?.toTypedArray() ?: arrayOfNulls(0)
        val displayError: String? = if (errorMessageParts.size == 2) {
            errorMessageParts[1]
        } else {
            errorMessage
        }
        builder.setMessage(displayError)
        builder.setNegativeButton(R.string.copy_to_clipboard) { _: DialogInterface?, _: Int ->
            chatsActivity?.copyTextToClipboard(displayError, R.string.error_message)
            Toast.makeText(
                chatsActivity,
                R.string.error_message_copied_to_clipboard,
                Toast.LENGTH_SHORT
            )
                .show()
        }
        builder.setPositiveButton(R.string.confirm, null)
        builder.create().show()
    }

    private fun deleteFile(message: Message?) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setNegativeButton(R.string.cancel, null)
        builder.setTitle(R.string.delete_file_dialog)
        builder.setMessage(R.string.delete_file_dialog_msg)
        builder.setPositiveButton(
            R.string.confirm
        ) { _: DialogInterface?, _: Int ->
            if (chatsActivity?.xmppConnectionService?.fileBackend?.deleteFile(message) == true) {
                message?.isDeleted = true
                chatsActivity?.xmppConnectionService?.evictPreview(message?.uuid)
                chatsActivity?.xmppConnectionService?.updateMessage(message, false)
                chatsActivity?.onConversationsListItemUpdated()
                refresh()
            }
        }
        builder.create().show()
    }

    private fun resendMessage(message: Message?) {
        if (message?.isFileOrImage == true) {
            if (message.conversation !is Conversation) {
                return
            }
            val conversation = message.conversation as Conversation
            val file = chatsActivity!!.xmppConnectionService.fileBackend.getFile(message)
            if (file.exists() && file.canRead() || message.hasFileOnRemoteHost()) {
                val xmppConnection = conversation.account.xmppConnection
                if (!message.hasFileOnRemoteHost()
                    && xmppConnection != null && conversation.mode == Conversational.MODE_SINGLE && !xmppConnection
                        .features
                        .httpUpload(message.fileParams.getSize())
                ) {
                    chatsActivity!!.selectPresence(
                        conversation
                    ) {
                        message.counterpart = conversation.nextCounterpart
                        chatsActivity!!.xmppConnectionService.resendFailedMessages(message)
                        Handler(Looper.getMainLooper())
                            .post {
                                val size = messageList.size
                                binding.rvMessagesList.setSelection(
                                    size - 1
                                )
                            }
                    }
                    return
                }
            } else if (!Compatibility.hasStoragePermission(activity)) {
                Toast.makeText(chatsActivity, R.string.no_storage_permission, Toast.LENGTH_SHORT).show()
                return
            } else {
                Toast.makeText(chatsActivity, R.string.file_deleted, Toast.LENGTH_SHORT).show()
                message.isDeleted = true
                chatsActivity!!.xmppConnectionService.updateMessage(message, false)
                chatsActivity!!.onConversationsListItemUpdated()
                refresh()
                return
            }
        }
        chatsActivity?.xmppConnectionService?.resendFailedMessages(message)
        Handler(Looper.getMainLooper()).post {
            val size = messageList.size
            binding.rvMessagesList.setSelection(size - 1)
        }
    }

    private fun cancelTransmission(message: Message?) {
        val transferable = message?.transferable
        if (transferable != null) {
            transferable.cancel()
        } else if (message?.status != Message.STATUS_RECEIVED) {
            chatsActivity?.xmppConnectionService?.markMessage(
                message, Message.STATUS_SEND_FAILED, Message.ERROR_MESSAGE_CANCELLED
            )
        }
    }

    private fun retryDecryption(message: Message?) {
        message?.encryption = Message.ENCRYPTION_PGP
        chatsActivity?.onConversationsListItemUpdated()
        refresh()
        conversation?.account?.pgpDecryptionService?.decrypt(message, false)
    }

    fun privateMessageWith(counterpart: Jid?) {
        if (conversation?.setOutgoingChatState(Config.DEFAULT_CHAT_STATE) == true) {
            chatsActivity?.xmppConnectionService?.sendChatState(conversation)
        }
        binding.editMessageInput.setText("")
        conversation?.nextCounterpart = counterpart
        updateSendButton()
        updateEditablity()
    }

    private fun correctMessage(message: Message?) {
        var messageModel = message
        while (messageModel!!.mergeable(messageModel.next())) {
            messageModel = messageModel.next()
        }
        conversation?.correctingMessage = messageModel
        val editable = binding.editMessageInput.text
        conversation?.draftMessage = editable.toString()
        binding.editMessageInput.setText("")
        binding.editMessageInput.append(messageModel.body)
    }

    private fun highlightInConference(nick: String) = with(binding) {
        val editable = editMessageInput.text
        val oldString = editable.toString().trim { it <= ' ' }
        val pos = editMessageInput.selectionStart
        if (oldString.isEmpty() || pos == 0) {
            editable!!.insert(0, "$nick: ")
        } else {
            val before = editable!![pos - 1]
            val after = if (editable.length > pos) editable[pos] else '\u0000'
            if (before == '\n') {
                editable.insert(pos, "$nick: ")
            } else {
                if (pos > 2 && editable.subSequence(pos - 2, pos).toString() == ": ") {
                    if (NickValidityChecker.check(
                            conversation, listOf(
                                *editable.subSequence(0, pos - 2).toString().split(", ").toTypedArray()
                            )
                        )
                    ) {
                        editable.insert(pos - 2, ", $nick")
                        return@with
                    }
                }
                editable.insert(
                    pos,
                    (if (Character.isWhitespace(before)) "" else " ")
                        + nick
                        + if (Character.isWhitespace(after)) "" else " "
                )
                if (Character.isWhitespace(after)) {
                    editMessageInput.setSelection(editMessageInput.selectionStart + 1)
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun startActivityForResult(intent: Intent, requestCode: Int) {
        val activity: Activity? = activity
        if (activity is ChatsActivity) {
            activity.clearPendingViewIntent()
        }
        super.startActivityForResult(intent, requestCode)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (conversation != null) {
            outState.putString(STATE_CONVERSATION_UUID, conversation!!.uuid)
            outState.putString(STATE_LAST_MESSAGE_UUID, lastMessageUuid)
            val uri = pendingTakePhotoUri.peek()
            if (uri != null) {
                outState.putString(STATE_PHOTO_URI, uri.toString())
            }
            val scrollState = scrollPosition
            if (scrollState != null) {
                outState.putParcelable(STATE_SCROLL_POSITION, scrollState)
            }
            val attachments = if (mediaPreviewAdapter == null) ArrayList() else mediaPreviewAdapter?.attachments
            if ((attachments?.size ?: 0) > 0) {
                outState.putParcelableArrayList(STATE_MEDIA_PREVIEWS, attachments)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState == null) {
            return
        }
        val uuid = savedInstanceState.getString(STATE_CONVERSATION_UUID)
        val attachments = savedInstanceState.getParcelableArrayList<Attachment>(STATE_MEDIA_PREVIEWS)
        pendingLastMessageUuid.push(savedInstanceState.getString(STATE_LAST_MESSAGE_UUID, null))
        if (uuid != null) {
            QuickLoader.set(uuid)
            pendingConversationsUuid.push(uuid)
            if (attachments != null && attachments.size > 0) {
                pendingMediaPreviews.push(attachments)
            }
            val takePhotoUri = savedInstanceState.getString(STATE_PHOTO_URI)
            if (takePhotoUri != null) {
                pendingTakePhotoUri.push(Uri.parse(takePhotoUri))
            }
            pendingScrollState.push(savedInstanceState.getParcelable(STATE_SCROLL_POSITION))
        }
    }

    override fun onStart() {
        super.onStart()
        if (reInitRequiredOnStart && conversation != null) {
            val extras = pendingExtras.pop()
            reInit(conversation, extras != null)
            extras?.let { processExtras(it) }
        } else if (conversation == null && chatsActivity != null && chatsActivity!!.xmppConnectionService != null) {
            val uuid = pendingConversationsUuid.pop()
            Timber.d("ConversationFragment.onStart() - activity was bound but no conversation loaded. uuid=" + uuid)
            uuid?.let { findAndReInitByUuidOrArchive(it) }
        }
    }

    override fun onStop() {
        super.onStop()
        val activity: Activity? = activity
        messageListAdapter!!.unregisterListenerInAudioPlayer()
        if (activity == null || !activity.isChangingConfigurations) {
            SoftKeyboardUtils.hideSoftKeyboard(activity)
            messageListAdapter!!.stopAudioPlayer()
        }
        if (conversation != null) {
            val msg = binding.editMessageInput.text.toString()
            storeNextMessage(msg)
            updateChatState(conversation, msg)
            chatsActivity!!.xmppConnectionService.notificationService.setOpenConversation(null)
        }
        reInitRequiredOnStart = true
    }

    private fun updateChatState(conversation: Conversation?, msg: String) {
        val state = if (msg.length == 0) Config.DEFAULT_CHAT_STATE else ChatState.PAUSED
        val status = conversation!!.account.status
        if (status == Account.State.ONLINE && conversation.setOutgoingChatState(state)) {
            chatsActivity!!.xmppConnectionService.sendChatState(conversation)
        }
    }

    private fun saveMessageDraftStopAudioPlayer() {
        val previousConversation = conversation
        if (chatsActivity == null || !isBindingNotNull || previousConversation == null) {
            return
        }
        Timber.d("ConversationFragment.saveMessageDraftStopAudioPlayer()")
        val msg = binding.editMessageInput.text.toString()
        storeNextMessage(msg)
        updateChatState(conversation, msg)
        messageListAdapter!!.stopAudioPlayer()
        mediaPreviewAdapter!!.clearPreviews()
        toggleInputMethod()
    }

    fun reInit(conversation: Conversation, extras: Bundle?) {
        Timber.d("reInit + ${extras.toString()}")
        QuickLoader.set(conversation.uuid)
        val changedConversation = this.conversation !== conversation
        if (changedConversation) {
            saveMessageDraftStopAudioPlayer()
        }
        clearPending()
        if (this.reInit(conversation, extras != null)) {
            extras?.let { processExtras(it) }
            reInitRequiredOnStart = false
        } else {
            reInitRequiredOnStart = true
            pendingExtras.push(extras)
        }
        resetUnreadMessagesCount()
    }

    private fun reInit(conversation: Conversation) {
        reInit(conversation, false)
    }

    private fun reInit(conversation: Conversation?, hasExtras: Boolean): Boolean {
        if (conversation == null) {
            return false
        }
        this.conversation = conversation
        // once we set the conversation all is good and it will automatically do the right thing in
        // onStart()
        if (chatsActivity == null || !isBindingNotNull) {
            return false
        }
        if (!chatsActivity!!.xmppConnectionService.isConversationStillOpen(this.conversation)) {
            chatsActivity!!.onConversationArchived(this.conversation!!)
            return false
        }
        stopScrolling()
        Timber.d("reInit(hasExtras=" + hasExtras + ")")
        if (this.conversation!!.isRead && hasExtras) {
            Timber.d("trimming conversation")
            this.conversation!!.trim()
        }
        setupIme()
        val scrolledToBottomAndNoPending = this.scrolledToBottom() && pendingScrollState.peek() == null
        binding.imageSend.contentDescription =
            chatsActivity!!.getString(R.string.send_message_to_x, conversation.name)
        binding.editMessageInput.setKeyboardListener(null)
        binding.editMessageInput.setText("")
        val participating = (conversation.mode == Conversational.MODE_SINGLE
            || conversation.mucOptions.participating())
        if (participating) {
            binding.editMessageInput.append(this.conversation!!.nextMessage)
        }
        binding.editMessageInput.setKeyboardListener(this)
        messageListAdapter!!.updatePreferences()
        refresh(false)
        chatsActivity!!.invalidateOptionsMenu()
        this.conversation!!.messagesLoaded.set(true)
        Timber.d("scrolledToBottomAndNoPending=$scrolledToBottomAndNoPending")
        if (hasExtras || scrolledToBottomAndNoPending) {
            //resetUnreadMessagesCount()
            synchronized(messageList) {
                Timber.d("jump to first unread message")
                val first = conversation.firstUnreadMessage
                val bottom = Math.max(0, messageList.size - 1)
                val pos: Int
                val jumpToBottom: Boolean
                if (first == null) {
                    pos = bottom
                    jumpToBottom = true
                } else {
                    val i = getIndexOf(first.uuid, messageList)
                    pos = if (i < 0) bottom else i
                    jumpToBottom = false
                }
                setSelection(pos, jumpToBottom)
            }
        }
        binding.rvMessagesList.post { fireReadEvent() }
        // TODO if we only do this when this fragment is running on main it won't *bing* in tablet
        // layout which might be unnecessary since we can *see* it
        chatsActivity!!.xmppConnectionService
            .notificationService
            .setOpenConversation(this.conversation)
        return true
    }

    private fun resetUnreadMessagesCount() {
        lastMessageUuid = null
        hideUnreadMessagesCount()
    }

    private fun hideUnreadMessagesCount() {
        if (isBindingNotNull) {
            binding.scrollToBottomButton.isEnabled = false
            binding.scrollToBottomButton.hide()
        }
    }

    private fun setSelection(pos: Int, jumpToBottom: Boolean) {
        ListViewUtils.setSelection(binding.rvMessagesList, pos, jumpToBottom)
        binding.rvMessagesList.post { ListViewUtils.setSelection(binding.rvMessagesList, pos, jumpToBottom) }
        binding.rvMessagesList.post { fireReadEvent() }
    }

    private fun scrolledToBottom(): Boolean {
        return isBindingNotNull && scrolledToBottom(
            binding.rvMessagesList
        )
    }

    private fun processExtras(extras: Bundle) {
        val downloadUuid = extras.getString(ChatsActivity.EXTRA_DOWNLOAD_UUID)
        val text = extras.getString(Intent.EXTRA_TEXT)
        val nick = extras.getString(ChatsActivity.EXTRA_NICK)
        val postInitAction = extras.getString(ChatsActivity.EXTRA_POST_INIT_ACTION)
        val asQuote = extras.getBoolean(ChatsActivity.EXTRA_AS_QUOTE)
        val pm = extras.getBoolean(ChatsActivity.EXTRA_IS_PRIVATE_MESSAGE, false)
        val doNotAppend = extras.getBoolean(ChatsActivity.EXTRA_DO_NOT_APPEND, false)
        val type = extras.getString(ChatsActivity.EXTRA_TYPE)
        val uris = extractUris(extras)
        if (uris != null && uris.size > 0) {
            if (uris.size == 1 && "geo" == uris[0].scheme) {
                mediaPreviewAdapter!!.addMediaPreviews(
                    Attachment.of(activity, uris[0], Attachment.Type.LOCATION)
                )
            } else {
                val cleanedUris = cleanUris(ArrayList(uris))
                mediaPreviewAdapter!!.addMediaPreviews(
                    Attachment.of(activity, cleanedUris, type)
                )
            }
            toggleInputMethod()
            return
        }
        if (nick != null) {
            if (pm) {
                val jid = conversation!!.jid
                try {
                    val next = Jid.of(jid.local, jid.domain, nick)
                    privateMessageWith(next)
                } catch (ignored: IllegalArgumentException) {
                    // do nothing
                }
            } else {
                val mucOptions = conversation!!.mucOptions
                if (mucOptions.participating() || conversation!!.nextCounterpart != null) {
                    highlightInConference(nick)
                }
            }
        } else {
            if (text != null && GeoHelper.GEO_URI.matcher(text).matches()) {
                mediaPreviewAdapter!!.addMediaPreviews(
                    Attachment.of(activity, Uri.parse(text), Attachment.Type.LOCATION)
                )
                toggleInputMethod()
                return
            } else if (text != null && asQuote) {
                quoteText(text)
            } else {
                appendText(text, doNotAppend)
            }
        }
        if (ChatsActivity.POST_ACTION_RECORD_VOICE == postInitAction) {
            attachFile(ATTACHMENT_CHOICE_RECORD_VOICE, false)
            return
        }
        val message = if (downloadUuid == null) null else conversation!!.findMessageWithFileAndUuid(downloadUuid)
        message?.let { startDownloadable(it) }
    }

    private fun extractUris(extras: Bundle): List<Uri>? {
        val uris: List<Uri>? = extras.getParcelableArrayList(Intent.EXTRA_STREAM)
        if (uris != null) {
            return uris
        }
        val uri = extras.getParcelable<Uri>(Intent.EXTRA_STREAM)
        return uri?.let { listOf(it) }
    }

    private fun cleanUris(uris: MutableList<Uri>): List<Uri> {
        val iterator = uris.iterator()
        while (iterator.hasNext()) {
            val uri = iterator.next()
            if (FileBackend.weOwnFile(uri)) {
                iterator.remove()
                Toast.makeText(
                    activity,
                    R.string.security_violation_not_attaching_file,
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
        return uris
    }

    private fun showBlockSubmenu(view: View): Boolean {
        val jid = conversation!!.jid
        val showReject = (!conversation!!.isWithStranger
            && conversation
            ?.contact
            ?.getOption(Contact.Options.PENDING_SUBSCRIPTION_REQUEST) == true)
        val popupMenu = PopupMenu(activity, view)
        popupMenu.inflate(R.menu.block)
        popupMenu.menu.findItem(R.id.block_contact).isVisible = jid.local != null
        popupMenu.menu.findItem(R.id.reject).isVisible = showReject
        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            val blockable: Blockable?
            val itemId = menuItem.itemId
            blockable = if (itemId == R.id.reject) {
                chatsActivity!!.xmppConnectionService.stopPresenceUpdatesTo(
                    conversation!!.contact
                )
                updateSnackBar(conversation)
                return@setOnMenuItemClickListener true
            } else if (itemId == R.id.block_domain) {
                conversation
                    ?.getAccount()
                    ?.roster
                    ?.getContact(jid.domain)
            } else {
                conversation
            }
            BlockContactDialog.show(chatsActivity, blockable)
            true
        }
        popupMenu.show()
        return true
    }

    private fun updateSnackBar(conversation: Conversation?) {
        val account = conversation!!.account
        val connection = account.xmppConnection
        val mode = conversation.mode
        val contact = if (mode == Conversation.MODE_SINGLE) conversation.contact else null
        if (conversation.status == Conversation.STATUS_ARCHIVED) {
            return
        }
        if (account.status == Account.State.DISABLED) {
            showSnackbar(
                R.string.this_account_is_disabled,
                R.string.enable,
                mEnableAccountListener
            )
        } else if (conversation.isBlocked) {
            showSnackbar(R.string.contact_blocked, R.string.unblock, mUnblockClickListener)
        } else if (contact != null && !contact.showInRoster()
            && contact.getOption(Contact.Options.PENDING_SUBSCRIPTION_REQUEST)
        ) {
            showSnackbar(
                R.string.contact_added_you,
                R.string.add_back,
                mAddBackClickListener,
                mLongPressBlockListener
            )
        } else if (contact != null
            && contact.getOption(Contact.Options.PENDING_SUBSCRIPTION_REQUEST)
        ) {
            showSnackbar(
                R.string.contact_asks_for_presence_subscription,
                R.string.allow,
                mAllowPresenceSubscription,
                mLongPressBlockListener
            )
        } else if (account.hasPendingPgpIntent(conversation)) {
            showSnackbar(R.string.openpgp_messages_found, R.string.decrypt, clickToDecryptListener)
        } else if (connection != null && connection.features.blocking()
            && conversation.countMessages() != 0 && !conversation.isBlocked
            && conversation.isWithStranger
        ) {
            showSnackbar(
                R.string.received_message_from_stranger, R.string.block, mBlockClickListener
            )
        } else {
            hideSnackbar()
        }
    }

    override fun refresh() {
        if (conversation != null && chatsActivity != null && chatsActivity!!.xmppConnectionService != null) {
            if (!chatsActivity!!.xmppConnectionService.isConversationStillOpen(conversation)) {
                chatsActivity!!.onConversationArchived(conversation!!)
                return
            }
        }
        this.refresh(true)
    }

    private fun refresh(notifyConversationRead: Boolean) {
        synchronized(messageList) {
            if (conversation != null) {
                conversation!!.populateWithMessages(messageList)
                updateSnackBar(conversation)
                updateStatusMessages()
                if (conversation!!.getReceivedMessagesCountSinceUuid(lastMessageUuid) != 0) {
                    //binding.unreadCountCustomView.setVisibility(View.VISIBLE);
                    //binding.unreadCountCustomView.setUnreadCount(conversation.getReceivedMessagesCountSinceUuid(lastMessageUuid));
                }
                messageListAdapter!!.notifyDataSetChanged()
                if (notifyConversationRead && chatsActivity != null) {
                    binding.rvMessagesList.post { fireReadEvent() }
                }
                updateSendButton()
                updateEditablity()
            }
        }
    }

    private fun messageSent() {
        sendingPgpMessage.set(false)
        binding.editMessageInput.setText("")
        if (conversation!!.setCorrectingMessage(null)) {
            binding.editMessageInput.append(conversation!!.draftMessage)
            conversation!!.draftMessage = null
        }
        storeNextMessage()
        val p = PreferenceManager.getDefaultSharedPreferences(chatsActivity)
        val prefScrollToBottom = p.getBoolean(
            "scroll_to_bottom",
            chatsActivity!!.resources.getBoolean(R.bool.scroll_to_bottom)
        )
        if (prefScrollToBottom || scrolledToBottom()) {
            Handler(Looper.getMainLooper())
                .post {
                    val size = messageList.size
                    binding.rvMessagesList.setSelection(size - 1)
                }
        }
    }

    private fun storeNextMessage(msg: String = binding.editMessageInput.text.toString()): Boolean {
        val participating = (conversation!!.mode == Conversational.MODE_SINGLE
            || conversation!!.mucOptions.participating())
        if (conversation!!.status != Conversation.STATUS_ARCHIVED && participating
            && conversation!!.setNextMessage(msg)
        ) {
            chatsActivity!!.xmppConnectionService.updateConversation(conversation)
            return true
        }
        return false
    }

    fun doneSendingPgpMessage() {
        sendingPgpMessage.set(false)
    }

    private fun getMaxHttpUploadSize(conversation: Conversation): Long {
        val connection = conversation.account.xmppConnection
        return connection?.features?.maxHttpUploadSize ?: -1
    }

    private fun updateEditablity() = with(binding) {
        val canWrite = (conversation!!.mode == Conversation.MODE_SINGLE || conversation!!.mucOptions.participating()
            || conversation!!.nextCounterpart != null)
        binding.editMessageInput.isFocusable = canWrite
        binding.editMessageInput.isFocusableInTouchMode = canWrite
        binding.imageSend.isEnabled = canWrite
        binding.editMessageInput.isCursorVisible = canWrite
        binding.editMessageInput.isEnabled = canWrite
    }

    private fun updateSendButton() {
        val hasAttachments = mediaPreviewAdapter != null && mediaPreviewAdapter?.hasAttachments() == true
        val c = conversation
        val text = if (!isBindingNotNull) "" else binding.editMessageInput.text.toString()
        val action: SendButtonAction = if (hasAttachments) {
            SendButtonAction.TEXT
        } else {
            SendButtonTool.getAction(activity, c, text)
        }
        val status: Presence.Status = if (c!!.account.status == Account.State.ONLINE) {
            if (chatsActivity != null && chatsActivity!!.xmppConnectionService != null && chatsActivity!!.xmppConnectionService.messageArchiveService.isCatchingUp(
                    c
                )
            ) {
                Presence.Status.OFFLINE
            } else if (c.mode == Conversation.MODE_SINGLE) {
                c.contact.shownStatus
            } else {
                if (c.mucOptions.online()) Presence.Status.ONLINE else Presence.Status.OFFLINE
            }
        } else {
            Presence.Status.OFFLINE
        }
        binding.imageSend.tag = action
        val activity: Activity? = activity
    }

    private fun updateStatusMessages() {
        DateSeparator.addAll(messageList)
        if (showLoadMoreMessages(conversation)) {
            messageList.add(0, Message.createLoadMoreMessage(conversation))
        }
        if (conversation!!.mode == Conversation.MODE_SINGLE) {
            val state = conversation!!.incomingChatState
            if (state == ChatState.COMPOSING) {
                messageList.add(
                    Message.createStatusMessage(
                        conversation,
                        getString(R.string.contact_is_typing, conversation!!.name)
                    )
                )
            } else if (state == ChatState.PAUSED) {
                messageList.add(
                    Message.createStatusMessage(
                        conversation,
                        getString(
                            R.string.contact_has_stopped_typing,
                            conversation!!.name
                        )
                    )
                )
            } else {
                for (i in messageList.indices.reversed()) {
                    val message = messageList[i]
                    if (message.type != Message.TYPE_STATUS) {
                        if (message.status == Message.STATUS_RECEIVED) {
                            return
                        } else {
                            if (message.status == Message.STATUS_SEND_DISPLAYED) {
                                messageList.add(
                                    i + 1,
                                    Message.createStatusMessage(
                                        conversation,
                                        getString(
                                            R.string.contact_has_read_up_to_this_point,
                                            conversation!!.name
                                        )
                                    )
                                )
                                return
                            }
                        }
                    }
                }
            }
        } else {
            val mucOptions = conversation!!.mucOptions
            val allUsers: List<MucOptions.User> = mucOptions.users
            val addedMarkers: MutableSet<ReadByMarker> = HashSet()
            var state = ChatState.COMPOSING
            var users: List<MucOptions.User> = conversation!!.mucOptions.getUsersWithChatState(state, 5)
            if (users.size == 0) {
                state = ChatState.PAUSED
                users = conversation!!.mucOptions.getUsersWithChatState(state, 5)
            }
            if (mucOptions.isPrivateAndNonAnonymous) {
                for (i in messageList.indices.reversed()) {
                    val markersForMessage = messageList[i].readByMarkers
                    val shownMarkers: MutableList<MucOptions.User> = ArrayList()
                    for (marker in markersForMessage) {
                        if (!ReadByMarker.contains(marker, addedMarkers)) {
                            addedMarkers.add(
                                marker
                            ) // may be put outside this condition. set should do
                            // dedup anyway
                            val user = mucOptions.findUser(marker)
                            if (user != null && !users.contains(user)) {
                                shownMarkers.add(user)
                            }
                        }
                    }
                    val markerForSender = ReadByMarker.from(messageList[i])
                    val statusMessage: Message?
                    val size = shownMarkers.size
                    if (size > 1) {
                        val body: String
                        body = if (size <= 4) {
                            getString(
                                R.string.contacts_have_read_up_to_this_point,
                                UIHelper.concatNames(shownMarkers)
                            )
                        } else if (ReadByMarker.allUsersRepresented(
                                allUsers, markersForMessage, markerForSender
                            )
                        ) {
                            getString(R.string.everyone_has_read_up_to_this_point)
                        } else {
                            getString(
                                R.string.contacts_and_n_more_have_read_up_to_this_point,
                                UIHelper.concatNames(shownMarkers, 3),
                                size - 3
                            )
                        }
                        statusMessage = Message.createStatusMessage(conversation, body)
                        statusMessage.counterparts = shownMarkers
                    } else if (size == 1) {
                        statusMessage = Message.createStatusMessage(
                            conversation,
                            getString(
                                R.string.contact_has_read_up_to_this_point,
                                UIHelper.getDisplayName(shownMarkers[0])
                            )
                        )
                        statusMessage.counterpart = shownMarkers[0].fullJid
                        statusMessage.trueCounterpart = shownMarkers[0].realJid
                    } else {
                        statusMessage = null
                    }
                    if (statusMessage != null) {
                        messageList.add(i + 1, statusMessage)
                    }
                    addedMarkers.add(markerForSender)
                    if (ReadByMarker.allUsersRepresented(allUsers, addedMarkers)) {
                        break
                    }
                }
            }
            if (users.size > 0) {
                val statusMessage: Message
                if (users.size == 1) {
                    val user = users[0]
                    val id =
                        if (state == ChatState.COMPOSING) R.string.contact_is_typing else R.string.contact_has_stopped_typing
                    statusMessage = Message.createStatusMessage(
                        conversation, getString(id, UIHelper.getDisplayName(user))
                    )
                    statusMessage.trueCounterpart = user.realJid
                    statusMessage.counterpart = user.fullJid
                } else {
                    val id =
                        if (state == ChatState.COMPOSING) R.string.contacts_are_typing else R.string.contacts_have_stopped_typing
                    statusMessage = Message.createStatusMessage(
                        conversation, getString(id, UIHelper.concatNames(users))
                    )
                    statusMessage.counterparts = users
                }
                messageList.add(statusMessage)
            }
        }
    }

    private fun stopScrolling() {
        val now = SystemClock.uptimeMillis()
        val cancel = MotionEvent.obtain(now, now, MotionEvent.ACTION_CANCEL, 0f, 0f, 0)
        binding.rvMessagesList.dispatchTouchEvent(cancel)
    }

    private fun showLoadMoreMessages(c: Conversation?): Boolean {
        if (chatsActivity == null || chatsActivity!!.xmppConnectionService == null) {
            return false
        }
        val mam = hasMamSupport(c) && !c!!.contact.isBlocked
        val service = chatsActivity!!.xmppConnectionService.messageArchiveService
        return (mam
            && (c!!.lastClearHistory.timestamp != 0L || (c.countMessages() == 0 && c.messagesLoaded.get()
            && c.hasMessagesLeftOnServer()
            && !service.queryInProgress(c))))
    }

    private fun hasMamSupport(c: Conversation?): Boolean {
        return if (c!!.mode == Conversation.MODE_SINGLE) {
            val connection = c.account.xmppConnection
            connection != null && connection.features.mam()
        } else {
            c.mucOptions.mamSupport()
        }
    }

    private fun showSnackbar(
        message: Int,
        action: Int,
        clickListener: View.OnClickListener?,
        longClickListener: OnLongClickListener? = null
    ) = with(binding) {
        snackbar.visibility = View.VISIBLE
        snackbar.setOnClickListener(null)
        snackbarMessage.setText(message)
        snackbarMessage.setOnClickListener(null)
        snackbarAction.isVisible = clickListener != null
        if (action != 0) {
            snackbarAction.setText(action)
        }
        snackbarAction.setOnClickListener(clickListener)
        snackbarAction.setOnLongClickListener(longClickListener)
    }

    private fun hideSnackbar() {
        binding.snackbar.visibility = View.GONE
    }

    private fun sendMessage(message: Message?) {
        chatsActivity!!.xmppConnectionService.sendMessage(message)
        messageSent()
    }

    private fun sendPgpMessage(message: Message) {
        val xmppService = chatsActivity!!.xmppConnectionService
        val contact = message.conversation.contact
        if (!chatsActivity!!.hasPgp()) {
            chatsActivity!!.showInstallPgpDialog()
            return
        }
        if (conversation!!.account.pgpSignature == null) {
            chatsActivity!!.announcePgp(
                conversation!!.account, conversation, null, chatsActivity!!.onOpenPGPKeyPublished
            )
            return
        }
        if (!sendingPgpMessage.compareAndSet(false, true)) {
            Timber.d("sending pgp message already in progress")
        }
        if (conversation!!.mode == Conversation.MODE_SINGLE) {
            if (contact.pgpKeyId != 0L) {
                xmppService
                    .pgpEngine
                    .hasKey(
                        contact,
                        object : UiCallback<Contact?> {
                            override fun userInputRequired(
                                pi: PendingIntent, contact: Contact?
                            ) {
                                startPendingIntent(pi, REQUEST_ENCRYPT_MESSAGE)
                            }

                            override fun success(contact: Contact?) {
                                encryptTextMessage(message)
                            }

                            override fun error(error: Int, contact: Contact?) {
                                chatsActivity!!.runOnUiThread {
                                    Toast.makeText(
                                        chatsActivity,
                                        R.string.unable_to_connect_to_keychain,
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                                sendingPgpMessage.set(false)
                            }
                        })
            } else {
                showNoPGPKeyDialog(
                    false
                ) { dialog: DialogInterface?, which: Int ->
                    conversation!!.nextEncryption = Message.ENCRYPTION_NONE
                    xmppService.updateConversation(conversation)
                    message.encryption = Message.ENCRYPTION_NONE
                    xmppService.sendMessage(message)
                    messageSent()
                }
            }
        } else {
            if (conversation!!.mucOptions.pgpKeysInUse()) {
                if (!conversation!!.mucOptions.everybodyHasKeys()) {
                    val warning = Toast.makeText(
                        activity, R.string.missing_public_keys, Toast.LENGTH_LONG
                    )
                    warning.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                    warning.show()
                }
                encryptTextMessage(message)
            } else {
                showNoPGPKeyDialog(
                    true
                ) { dialog: DialogInterface?, which: Int ->
                    conversation!!.nextEncryption = Message.ENCRYPTION_NONE
                    message.encryption = Message.ENCRYPTION_NONE
                    xmppService.updateConversation(conversation)
                    xmppService.sendMessage(message)
                    messageSent()
                }
            }
        }
    }

    fun encryptTextMessage(message: Message?) {
        chatsActivity!!.xmppConnectionService
            .pgpEngine
            .encrypt(
                message,
                object : UiCallback<Message?> {
                    override fun userInputRequired(pi: PendingIntent, message: Message?) {
                        startPendingIntent(pi, REQUEST_SEND_MESSAGE)
                    }

                    override fun success(message: Message?) {
                        // TODO the following two call can be made before the callback
                        requireActivity().runOnUiThread { messageSent() }
                    }

                    override fun error(error: Int, message: Message?) {
                        requireActivity().runOnUiThread {
                            doneSendingPgpMessage()
                            Toast.makeText(
                                requireActivity(),
                                if (error == 0) R.string.unable_to_connect_to_keychain
                                else error, Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
    }

    private fun showNoPGPKeyDialog(plural: Boolean, listener: DialogInterface.OnClickListener?) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setIconAttribute(android.R.attr.alertDialogIcon)
        if (plural) {
            builder.setTitle(getString(R.string.no_pgp_keys))
            builder.setMessage(getText(R.string.contacts_have_no_pgp_keys))
        } else {
            builder.setTitle(getString(R.string.no_pgp_key))
            builder.setMessage(getText(R.string.contact_has_no_pgp_key))
        }
        builder.setNegativeButton(getString(R.string.cancel), null)
        builder.setPositiveButton(getString(R.string.send_unencrypted), listener)
        builder.create().show()
    }

    private fun appendText(text: String?, doNotAppend: Boolean) {
        var chatText = text ?: return
        val editable = binding.editMessageInput.text
        val previous = editable?.toString() ?: ""
        if (doNotAppend && !TextUtils.isEmpty(previous)) {
            Toast.makeText(activity, R.string.already_drafting_message, Toast.LENGTH_LONG)
                .show()
            return
        }
        if (UIHelper.isLastLineQuote(previous)) {
            chatText = """
                
                $chatText
                """.trimIndent()
        } else if (previous.length != 0
            && !Character.isWhitespace(previous[previous.length - 1])
        ) {
            chatText = " $chatText"
        }
        binding.editMessageInput.append(chatText)
    }

    override fun onEnterPressed(isCtrlPressed: Boolean): Boolean {
        if (isCtrlPressed || enterIsSend()) {
            sendMessage()
            return true
        }
        return false
    }

    private fun enterIsSend(): Boolean {
        val p = PreferenceManager.getDefaultSharedPreferences(activity)
        return p.getBoolean("enter_is_send", resources.getBoolean(R.bool.enter_is_send))
    }

    fun onArrowUpCtrlPressed(): Boolean {
        val lastEditableMessage = if (conversation == null) null else conversation!!.lastEditableMessage
        return if (lastEditableMessage != null) {
            correctMessage(lastEditableMessage)
            true
        } else {
            Toast.makeText(activity, R.string.could_not_correct_message, Toast.LENGTH_LONG)
                .show()
            false
        }
    }

    override fun onTypingStarted() {
        val service = (if (chatsActivity == null) null else chatsActivity!!.xmppConnectionService) ?: return
        val status = conversation!!.account.status
        if (status == Account.State.ONLINE
            && conversation!!.setOutgoingChatState(ChatState.COMPOSING)
        ) {
            service.sendChatState(conversation)
        }
        runOnUiThread { updateSendButton() }
    }

    override fun onTypingStopped() {
        val service = (if (chatsActivity == null) null else chatsActivity!!.xmppConnectionService) ?: return
        val status = conversation!!.account.status
        if (status == Account.State.ONLINE && conversation!!.setOutgoingChatState(ChatState.PAUSED)) {
            service.sendChatState(conversation)
        }
    }

    override fun onTextDeleted() {
        val service = (if (chatsActivity == null) null else chatsActivity!!.xmppConnectionService) ?: return
        val status = conversation!!.account.status
        if (status == Account.State.ONLINE
            && conversation!!.setOutgoingChatState(Config.DEFAULT_CHAT_STATE)
        ) {
            service.sendChatState(conversation)
        }
        if (storeNextMessage()) {
            runOnUiThread {
                if (chatsActivity == null) {
                    return@runOnUiThread
                }
                chatsActivity!!.onConversationsListItemUpdated()
            }
        }
        runOnUiThread { updateSendButton() }
    }

    override fun onTextChanged() {
        if (conversation != null && conversation!!.correctingMessage != null) {
            runOnUiThread { updateSendButton() }
        }
    }

    override fun onTabPressed(repeated: Boolean): Boolean {
        if (conversation == null || conversation!!.mode == Conversation.MODE_SINGLE) {
            return false
        }
        if (repeated) {
            completionIndex++
        } else {
            lastCompletionLength = 0
            completionIndex = 0
            val content = binding.editMessageInput.text.toString()
            lastCompletionCursor = binding.editMessageInput.selectionEnd
            val start = if (lastCompletionCursor > 0) content.lastIndexOf(" ", lastCompletionCursor - 1) + 1 else 0
            firstWord = start == 0
            incomplete = content.substring(start, lastCompletionCursor)
        }
        val completions: MutableList<String> = ArrayList()
        for (user in conversation!!.mucOptions.users) {
            val name = user.name
            if (name != null && name.startsWith(incomplete!!)) {
                completions.add(name + if (firstWord) ": " else " ")
            }
        }
        completions.sort()
        if (completions.size > completionIndex) {
            val completion = completions[completionIndex].substring(incomplete!!.length)
            binding.editMessageInput
                .editableText
                .delete(lastCompletionCursor, lastCompletionCursor + lastCompletionLength)
            binding.editMessageInput.editableText.insert(lastCompletionCursor, completion)
            lastCompletionLength = completion.length
        } else {
            completionIndex = -1
            binding.editMessageInput
                .editableText
                .delete(lastCompletionCursor, lastCompletionCursor + lastCompletionLength)
            lastCompletionLength = 0
        }
        return true
    }

    private fun startPendingIntent(pendingIntent: PendingIntent, requestCode: Int) {
        try {
            requireActivity().startIntentSenderForResult(
                pendingIntent.intentSender, requestCode, null, 0, 0, 0
            )
        } catch (ignored: SendIntentException) {
        }
    }

    override fun onBackendConnected() {
        Timber.d("ConversationFragment.onBackendConnected()")
        val uuid = pendingConversationsUuid.pop()
        if (uuid != null) {
            if (!findAndReInitByUuidOrArchive(uuid)) {
                return
            }
        } else {
            if (!chatsActivity!!.xmppConnectionService.isConversationStillOpen(conversation)) {
                clearPending()
                chatsActivity!!.onConversationArchived(conversation!!)
                return
            }
        }
        val activityResult = postponedActivityResult.pop()
        activityResult?.let { handleActivityResult(it) }
        clearPending()
    }

    private fun findAndReInitByUuidOrArchive(uuid: String): Boolean {
        val conversation = chatsActivity!!.xmppConnectionService.findConversationByUuid(uuid)
        if (conversation == null) {
            clearPending()
            chatsActivity!!.onConversationArchived(null)
            return false
        }
        reInit(conversation)
        val scrollState = pendingScrollState.pop()
        val lastMessageUuid = pendingLastMessageUuid.pop()
        val attachments: List<Attachment>? = pendingMediaPreviews.pop()
        scrollState?.let { setScrollPosition(it, lastMessageUuid) }
        if (attachments != null && attachments.size > 0) {
            Timber.d("had attachments on restore")
            mediaPreviewAdapter!!.addMediaPreviews(attachments)
            toggleInputMethod()
        }
        return true
    }

    private fun clearPending() {
        if (postponedActivityResult.clear()) {
            Timber.e("cleared pending intent with unhandled result left")
            if (pendingTakePhotoUri.clear()) {
                Timber.e("cleared pending photo uri")
            }
        }
        if (pendingScrollState.clear()) {
            Timber.e("cleared scroll state")
        }
        if (pendingConversationsUuid.clear()) {
            Timber.e("cleared pending conversations uuid")
        }
        if (pendingMediaPreviews.clear()) {
            Timber.e("cleared pending media previews")
        }
    }

    override fun onContactPictureLongClicked(v: View, message: Message) {
        val fingerprint: String = if (message.encryption == Message.ENCRYPTION_PGP
            || message.encryption == Message.ENCRYPTION_DECRYPTED
        ) {
            "pgp"
        } else {
            message.fingerprint
        }
        val popupMenu = PopupMenu(activity, v)
        val contact = message.contact
        if (message.status <= Message.STATUS_RECEIVED
            && (contact == null || !contact.isSelf)
        ) {
            if (message.conversation.mode == Conversation.MODE_MULTI) {
                val cp = message.counterpart
                if (cp == null || cp.isBareJid) {
                    return
                }
                val tcp = message.trueCounterpart
                val userByRealJid =
                    if (tcp != null) conversation!!.mucOptions.findOrCreateUserByRealJid(tcp, cp) else null
                val user = userByRealJid ?: conversation!!.mucOptions.findUserByFullJid(cp)
                popupMenu.inflate(R.menu.muc_details_context)
                val menu = popupMenu.menu
                MucDetailsContextMenuHelper.configureMucDetailsContextMenu(
                    chatsActivity, menu, conversation, user
                )
                popupMenu.setOnMenuItemClickListener { menuItem: MenuItem? ->
                    MucDetailsContextMenuHelper.onContextItemSelected(
                        menuItem, user, chatsActivity, fingerprint
                    )
                }
            } else {
                popupMenu.inflate(R.menu.one_on_one_context)
                popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                    val itemId = item.itemId
                    if (itemId == R.id.action_contact_details) {
                        chatsActivity!!.switchToContactDetails(
                            message.contact, fingerprint
                        )
                    } else if (itemId == R.id.action_show_qr_code) {
                        chatsActivity!!.showQrCode(
                            "xmpp:"
                                + message.contact
                                .jid
                                .asBareJid()
                                .toEscapedString()
                        )
                    }
                    true
                }
            }
        } else {
            popupMenu.inflate(R.menu.account_context)
            val menu = popupMenu.menu
            menu.findItem(R.id.action_manage_accounts).isVisible = QuickConversationsService.isConversations()
            popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                val activity: XmppActivity? = chatsActivity
                if (activity == null) {
                    Timber.e("Unable to perform action. no context provided")
                    return@setOnMenuItemClickListener true
                }
                when (item.itemId) {
                    R.id.action_show_qr_code -> {
                        activity.showQrCode(conversation!!.account.shareableUri)
                    }
                    R.id.action_account_details -> {
                        activity.switchToAccount(
                            message.conversation.account, fingerprint
                        )
                    }
                    R.id.action_manage_accounts -> {
                        AccountUtils.launchManageAccounts(activity)
                    }
                }
                true
            }
        }
        popupMenu.show()
    }

    override fun onContactPictureClicked(message: Message) {
        val fingerprint: String
        fingerprint = if (message.encryption == Message.ENCRYPTION_PGP
            || message.encryption == Message.ENCRYPTION_DECRYPTED
        ) {
            "pgp"
        } else {
            message.fingerprint
        }
        val received = message.status <= Message.STATUS_RECEIVED
        if (received) {
            if (message.conversation is Conversation
                && message.conversation.mode == Conversation.MODE_MULTI
            ) {
                val tcp = message.trueCounterpart
                val user = message.counterpart
                if (user != null && !user.isBareJid) {
                    val mucOptions = (message.conversation as Conversation).mucOptions
                    if (mucOptions.participating()
                        || (message.conversation as Conversation).nextCounterpart
                        != null
                    ) {
                        if (!mucOptions.isUserInRoom(user)
                            && mucOptions.findUserByRealJid(
                                tcp?.asBareJid()
                            )
                            == null
                        ) {
                            Toast.makeText(
                                activity,
                                chatsActivity!!.getString(
                                    R.string.user_has_left_conference,
                                    user.resource
                                ),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                        highlightInConference(user.resource)
                    } else {
                        Toast.makeText(
                            activity,
                            R.string.you_are_not_participating,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
                return
            } else {
                if (!message.contact.isSelf) {
                    chatsActivity!!.switchToContactDetails(message.contact, fingerprint)
                    return
                }
            }
        }
        chatsActivity!!.switchToAccount(message.conversation.account, fingerprint)
    }

    companion object {
        const val REQUEST_SEND_MESSAGE = 0x0201
        const val REQUEST_DECRYPT_PGP = 0x0202
        const val REQUEST_ENCRYPT_MESSAGE = 0x0207
        const val REQUEST_TRUST_KEYS_TEXT = 0x0208
        const val REQUEST_TRUST_KEYS_ATTACHMENTS = 0x0209
        const val REQUEST_START_DOWNLOAD = 0x0210
        const val REQUEST_ADD_EDITOR_CONTENT = 0x0211
        const val REQUEST_COMMIT_ATTACHMENTS = 0x0212
        const val REQUEST_START_AUDIO_CALL = 0x213
        const val REQUEST_START_VIDEO_CALL = 0x214
        const val ATTACHMENT_CHOICE_CHOOSE_IMAGE = 0x0301
        const val ATTACHMENT_CHOICE_TAKE_PHOTO = 0x0302
        const val ATTACHMENT_CHOICE_CHOOSE_FILE = 0x0303
        const val ATTACHMENT_CHOICE_RECORD_VOICE = 0x0304
        const val ATTACHMENT_CHOICE_LOCATION = 0x0305
        const val ATTACHMENT_CHOICE_INVALID = 0x0306
        const val ATTACHMENT_CHOICE_RECORD_VIDEO = 0x0307
        const val RECENTLY_USED_QUICK_ACTION = "recently_used_quick_action"
        val STATE_CONVERSATION_UUID = ChatFragment::class.java.name + ".uuid"
        val STATE_SCROLL_POSITION = ChatFragment::class.java.name + ".scroll_position"
        val STATE_PHOTO_URI = ChatFragment::class.java.name + ".media_previews"
        val STATE_MEDIA_PREVIEWS = ChatFragment::class.java.name + ".take_photo_uri"
        private const val STATE_LAST_MESSAGE_UUID = "state_last_message_uuid"
        private fun findConversationFragment(activity: AppCompatActivity): ChatFragment? {
            val fragment = activity.supportFragmentManager.findFragmentById(R.id.main_fragment)
            return if (fragment is ChatFragment) {
                fragment
            } else null
        }

        fun startStopPending(activity: AppCompatActivity) {
            val fragment = findConversationFragment(activity)
            if (fragment != null) {
                fragment.messageListAdapter!!.startStopPending()
            }
        }

        fun downloadFile(activity: AppCompatActivity, message: Message?) {
            val fragment = findConversationFragment(activity)
            fragment?.startDownloadable(message)
        }

        fun registerPendingMessage(activity: AppCompatActivity, message: Message?) {
            val fragment = findConversationFragment(activity)
            fragment?.pendingMessage?.push(message)
        }

        fun openPendingMessage(activity: AppCompatActivity) {
            val fragment = findConversationFragment(activity)
            if (fragment != null) {
                val message = fragment.pendingMessage.pop()
                if (message != null) {
                    fragment.messageListAdapter!!.openDownloadable(message)
                }
            }
        }

        fun getConversation(activity: AppCompatActivity): Conversation? {
            return getConversation(activity, R.id.secondary_fragment)
        }

        private fun getConversation(activity: AppCompatActivity, @IdRes res: Int): Conversation? {
            val fragment = activity.supportFragmentManager.findFragmentById(res)
            return if (fragment is ChatFragment) {
                fragment.conversation
            } else {
                null
            }
        }

        operator fun get(activity: AppCompatActivity): ChatFragment? {
            val fragmentManager = activity.supportFragmentManager
            var fragment = fragmentManager.findFragmentById(R.id.main_fragment)
            return if (fragment is ChatFragment) {
                fragment
            } else {
                fragment = fragmentManager.findFragmentById(R.id.secondary_fragment)
                if (fragment is ChatFragment) fragment else null
            }
        }

        fun getConversationReliable(activity: AppCompatActivity): Conversation? {
            val conversation = getConversation(activity, R.id.secondary_fragment)
            return conversation
                ?: getConversation(
                    activity,
                    R.id.main_fragment
                )
        }

        private fun scrolledToBottom(listView: AbsListView): Boolean {
            val count = listView.count
            return if (count == 0) {
                true
            } else if (listView.lastVisiblePosition == count - 1) {
                val lastChild = listView.getChildAt(listView.childCount - 1)
                lastChild != null && lastChild.bottom <= listView.height
            } else {
                false
            }
        }

        private fun anyNeedsExternalStoragePermission(
            attachments: Collection<Attachment>
        ): Boolean {
            for (attachment in attachments) {
                if (attachment.type != Attachment.Type.LOCATION) {
                    return true
                }
            }
            return false
        }
    }
}