package com.topiichat.app.features.chats.activity

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.KeyEvent
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentManager
import com.topiichat.app.R
import com.topiichat.app.databinding.ActivityChatsBinding
import com.topiichat.app.features.chats.base.BaseChatFragment
import com.topiichat.app.features.chats.new_chat.NewChatFragment
import com.topiichat.app.features.chats.root.presentation.ChatsFragment
import dagger.hilt.android.AndroidEntryPoint
import eu.siacs.conversations.crypto.OmemoSetting
import eu.siacs.conversations.entities.Conversation
import eu.siacs.conversations.services.XmppConnectionService.OnAccountUpdate
import eu.siacs.conversations.services.XmppConnectionService.OnAffiliationChanged
import eu.siacs.conversations.services.XmppConnectionService.OnConversationUpdate
import eu.siacs.conversations.services.XmppConnectionService.OnRosterUpdate
import eu.siacs.conversations.services.XmppConnectionService.OnShowErrorToast
import eu.siacs.conversations.ui.UriHandlerActivity
import eu.siacs.conversations.ui.XmppActivity
import eu.siacs.conversations.ui.interfaces.OnBackendConnected
import eu.siacs.conversations.ui.interfaces.OnConversationArchived
import eu.siacs.conversations.ui.interfaces.OnConversationRead
import eu.siacs.conversations.ui.interfaces.OnConversationSelected
import eu.siacs.conversations.ui.interfaces.OnConversationsListItemUpdated
import eu.siacs.conversations.ui.util.ActivityResult
import eu.siacs.conversations.ui.util.ConversationMenuConfigurator
import eu.siacs.conversations.ui.util.MenuDoubleTabUtil
import eu.siacs.conversations.ui.util.PendingItem
import eu.siacs.conversations.utils.ExceptionHelper
import eu.siacs.conversations.xmpp.Jid
import eu.siacs.conversations.xmpp.OnUpdateBlocklist
import org.openintents.openpgp.util.OpenPgpApi
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

@AndroidEntryPoint
class ChatsActivity : XmppActivity(), OnConversationSelected, OnConversationArchived, OnConversationsListItemUpdated,
    OnConversationRead, OnAccountUpdate, OnConversationUpdate, OnRosterUpdate, OnUpdateBlocklist, OnShowErrorToast,
    OnAffiliationChanged {
    private val pendingViewIntent = PendingItem<Intent?>()
    private val postponedActivityResult = PendingItem<ActivityResult>()
    private lateinit var binding: ActivityChatsBinding
    private var activityPaused = true
    private val redirectInProcess = AtomicBoolean(false)

    override fun refreshUiReal() {
        invalidateOptionsMenu()
        refreshFragment(R.id.chats_container)
    }

    override fun onBackendConnected() {
        if (performRedirectIfNecessary(true)) {
            return
        }
        xmppConnectionService.notificationService.setIsInForeground(true)
        notifyFragmentOfBackendConnected(R.id.chats_container)
        val activityResult = postponedActivityResult.pop()
        activityResult?.let { handleActivityResult(it) }
        showDialogsIfMainIsOverview()
    }

    private fun performRedirectIfNecessary(noAnimation: Boolean): Boolean {
        return performRedirectIfNecessary(null, noAnimation)
    }

    private fun performRedirectIfNecessary(ignore: Conversation?, noAnimation: Boolean): Boolean {
        if (xmppConnectionService == null) {
            return false
        }
        val isConversationsListEmpty = xmppConnectionService.isConversationsListEmpty(ignore)
        if (isConversationsListEmpty && redirectInProcess.compareAndSet(false, true)) {
            /*val account = createMockAccount(xmppConnectionService)
            val intent = Intent(this, EditAccountActivity::class.java)
            intent.putExtra("jid", account!!.jid.asBareJid().toString())
            intent.putExtra("init", true)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            Toast.makeText(this, com.yourbestigor.chat.R.string.secure_password_generated, Toast.LENGTH_SHORT).show()
            runOnUiThread {
                startActivity(intent)
                if (noAnimation) {
                    overridePendingTransition(0, 0)
                }
            }*.

             */

            runOnUiThread {
                WelcomeActivity.launch(this)
            }
        }
        return redirectInProcess.get()
    }

    private fun showDialogsIfMainIsOverview() {
        if (xmppConnectionService == null) {
            return
        }
        val fragment = supportFragmentManager.findFragmentById(R.id.chats_container)
        if (fragment is ChatsFragment) {
            if (ExceptionHelper.checkForCrash(this)) {
                return
            }
            openBatteryOptimizationDialogIfNeeded()
        }
    }

    private val batteryOptimizationPreferenceKey: String
        get() {
            @SuppressLint("HardwareIds") val device =
                Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
            return "show_battery_optimization" + (device ?: "")
        }

    private fun setNeverAskForBatteryOptimizationsAgain() {
        preferences.edit().putBoolean(batteryOptimizationPreferenceKey, false).apply()
    }

    @SuppressLint("BatteryLife")
    @Suppress("DEPRECATION")
    private fun openBatteryOptimizationDialogIfNeeded() {
        if (isOptimizingBattery
            && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && preferences.getBoolean(
                batteryOptimizationPreferenceKey,
                true
            )
        ) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(com.yourbestigor.chat.R.string.battery_optimizations_enabled)
            builder.setMessage(
                getString(
                    com.yourbestigor.chat.R.string.battery_optimizations_enabled_dialog,
                    getString(R.string.app_name)
                )
            )
            builder.setPositiveButton(com.yourbestigor.chat.R.string.next) { _: DialogInterface?, _: Int ->
                val intent = Intent(
                    Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                )
                val uri = Uri.parse("package:$packageName")
                intent.data = uri
                try {
                    startActivityForResult(intent, REQUEST_BATTERY_OP)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(
                        this,
                        com.yourbestigor.chat.R.string.device_does_not_support_battery_op,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            builder.setOnDismissListener { setNeverAskForBatteryOptimizationsAgain() }
            val dialog = builder.create()
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
        }
    }

    private fun notifyFragmentOfBackendConnected(@IdRes id: Int) {
        val fragment = supportFragmentManager.findFragmentById(id)
        if (fragment is OnBackendConnected) {
            (fragment as OnBackendConnected).onBackendConnected()
        }
    }

    private fun refreshFragment(@IdRes id: Int) {
        val fragment = supportFragmentManager.findFragmentById(id)
        if (fragment is BaseChatFragment<*>) {
            fragment.refresh()
        }
    }

    private fun processViewIntent(intent: Intent): Boolean {
        Timber.d("Process view intent")
        val uuid = intent.getStringExtra(EXTRA_CONVERSATION)
        val conversation = if (uuid != null) xmppConnectionService.findConversationByUuid(uuid) else null
        if (conversation == null) {
            Timber.d("Unable to view conversation with uuid:$uuid")
            return false
        }
        openConversation(conversation, intent.extras)
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        UriHandlerActivity.onRequestPermissionResult(this, requestCode, grantResults)
        if (grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                when (requestCode) {
                    REQUEST_OPEN_MESSAGE -> {
                        refreshUiReal()
                        NewChatFragment.openPendingMessage(this)
                    }
                    REQUEST_PLAY_PAUSE -> NewChatFragment.startStopPending(
                        this
                    )
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val activityResult = ActivityResult.of(requestCode, resultCode, data)
        if (xmppConnectionService != null && activityResult.data != null) {
            handleActivityResult(activityResult)
        } else {
            postponedActivityResult.push(activityResult)
        }
    }

    private fun handleActivityResult(activityResult: ActivityResult) {
        if (activityResult.resultCode == RESULT_OK && activityResult.data != null) {
            handlePositiveActivityResult(activityResult.requestCode, activityResult.data)
        } else {
            handleNegativeActivityResult(activityResult.requestCode)
        }
    }

    private fun handleNegativeActivityResult(requestCode: Int) {
        val conversation = NewChatFragment.getConversationReliable(this)
        when (requestCode) {
            NewChatFragment.REQUEST_DECRYPT_PGP -> {
                if (conversation == null) {
                    return
                }
                conversation.account.pgpDecryptionService.giveUpCurrentDecryption()
            }
            REQUEST_BATTERY_OP -> setNeverAskForBatteryOptimizationsAgain()
        }
    }

    private fun handlePositiveActivityResult(requestCode: Int, data: Intent) {
        val conversation = NewChatFragment.getConversationReliable(this)
        if (conversation == null) {
            Timber.d("conversation not found")
            return
        }
        when (requestCode) {
            NewChatFragment.REQUEST_DECRYPT_PGP -> conversation.account.pgpDecryptionService.continueDecryption(
                data
            )
            REQUEST_CHOOSE_PGP_ID -> {
                val id = data.getLongExtra(OpenPgpApi.EXTRA_SIGN_KEY_ID, 0)
                if (id != 0L) {
                    conversation.account.setPgpSignId(id)
                    announcePgp(conversation.account, null, null, onOpenPGPKeyPublished)
                } else {
                    choosePgpSignId(conversation.account)
                }
            }
            REQUEST_ANNOUNCE_PGP -> announcePgp(conversation.account, conversation, data, onOpenPGPKeyPublished)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        ConversationMenuConfigurator.reloadFeatures(this)
        OmemoSetting.load(this)
        binding = ActivityChatsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.supportFragmentManager.addOnBackStackChangedListener { showDialogsIfMainIsOverview() }
        initializeFragments()
        val intent: Intent? = if (savedInstanceState == null) {
            intent
        } else {
            savedInstanceState.getParcelable("intent")
        }
        if (isViewOrShareIntent(intent)) {
            Timber.d("View or Share intent")
            pendingViewIntent.push(intent)
            setIntent(createLauncherIntent(this))
        }
    }

    override fun onConversationSelected(conversation: Conversation) {
        clearPendingViewIntent()
        if (NewChatFragment.getConversation(this) === conversation) {
            Timber.d("ignore onConversationSelected() because conversation is already open")
            return
        }
        openConversation(conversation, null)
    }

    fun clearPendingViewIntent() {
        if (pendingViewIntent.clear()) {
            Timber.e("cleared pending view intent")
        }
    }

    private fun displayToast(msg: String) {
        runOnUiThread { Toast.makeText(this@ChatsActivity, msg, Toast.LENGTH_SHORT).show() }
    }

    override fun onAffiliationChangedSuccessful(jid: Jid) {}
    override fun onAffiliationChangeFailed(jid: Jid, resId: Int) {
        displayToast(getString(resId, jid.asBareJid().toString()))
    }

    private fun openConversation(conversation: Conversation, extras: Bundle?) {
        Timber.d("Open conversations")
        val fragmentManager = supportFragmentManager
        executePendingTransactions(fragmentManager)
        val mainNeedsRefresh = false
        val mainFragment = fragmentManager.findFragmentById(R.id.chats_container)
        val conversationFragment: NewChatFragment
        if (mainFragment is NewChatFragment) {
            conversationFragment = mainFragment
        } else {
            conversationFragment = NewChatFragment()
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.chats_container, conversationFragment)
            fragmentTransaction.addToBackStack(null)
            try {
                fragmentTransaction.commit()
            } catch (e: IllegalStateException) {
                Timber.w(e, "sate loss while opening conversation")
                //allowing state loss is probably fine since view intents et all are already stored and a click can probably be 'ignored'
                return
            }
        }
        conversationFragment.reInit(conversation, extras ?: Bundle())
        if (mainNeedsRefresh) {
            refreshFragment(R.id.chats_container)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (MenuDoubleTabUtil.shouldIgnoreTap()) {
            return false
        }
        val itemId = item.itemId
        if (itemId == android.R.id.home) {
            val fm = supportFragmentManager
            if (fm.backStackEntryCount > 0) {
                try {
                    fm.popBackStack()
                } catch (e: IllegalStateException) {
                    Timber.w("Unable to pop back stack after pressing home button")
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onKeyDown(keyCode: Int, keyEvent: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP && keyEvent.isCtrlPressed) {
            val conversationFragment = NewChatFragment[this]
            if (conversationFragment != null && conversationFragment.onArrowUpCtrlPressed()) {
                return true
            }
        }
        return super.onKeyDown(keyCode, keyEvent)
    }

    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        val pendingIntent = pendingViewIntent.peek()
        savedInstanceState.putParcelable("intent", pendingIntent ?: intent)
        super.onSaveInstanceState(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        redirectInProcess.set(false)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (isViewOrShareIntent(intent)) {
            Timber.d("View or Share intent")
            if (xmppConnectionService != null) {
                clearPendingViewIntent()
                processViewIntent(intent)
            } else {
                Timber.d("connection service null")
                pendingViewIntent.push(intent)
            }
        }
        setIntent(createLauncherIntent(this))
    }

    override fun onPause() {
        activityPaused = true
        super.onPause()
    }

    public override fun onResume() {
        super.onResume()
        activityPaused = false
    }

    private fun initializeFragments() {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        val mainFragment = fragmentManager.findFragmentById(R.id.chats_container)
        if (mainFragment == null)
            transaction.replace(R.id.chats_container, ChatsFragment())
        transaction.commit()
    }

    override fun onConversationArchived(conversation: Conversation?) {
        if (performRedirectIfNecessary(conversation, false)) {
            return
        }
        val fragmentManager = supportFragmentManager
        val mainFragment = fragmentManager.findFragmentById(R.id.container)
        if (mainFragment is NewChatFragment) {
            try {
                fragmentManager.popBackStack()
            } catch (e: IllegalStateException) {
                Timber.w(e, "state loss while popping back state after archiving conversation")
                //this usually means activity is no longer active; meaning on the next open we will run through this again
            }
            return
        }
    }

    override fun onConversationsListItemUpdated() {
        val fragment = supportFragmentManager.findFragmentById(R.id.chats_container)
        //TODO
        if (fragment is ChatsFragment) {
            fragment.refresh()
        }
    }

    override fun switchToConversation(conversation: Conversation) {
        openConversation(conversation, null)
    }

    override fun onConversationRead(conversation: Conversation, upToUuid: String) {
        if (!activityPaused && pendingViewIntent.peek() == null) {
            xmppConnectionService.sendReadMarker(conversation, upToUuid)
        } else {
            Timber.d("ignoring read callback. mActivityPaused=$activityPaused")
        }
    }

    override fun onAccountUpdate() {
        refreshUi()
    }

    override fun onConversationUpdate() {
        if (performRedirectIfNecessary(false)) {
            return
        }
        refreshUi()
    }

    override fun onRosterUpdate() {
        refreshUi()
    }

    override fun OnUpdateBlocklist(status: OnUpdateBlocklist.Status) {
        refreshUi()
    }

    override fun onShowErrorToast(resId: Int) {
        runOnUiThread { Toast.makeText(this, resId, Toast.LENGTH_SHORT).show() }
    }

    companion object {
        const val ACTION_VIEW_CONVERSATION = "eu.siacs.conversations.action.VIEW"
        const val EXTRA_CONVERSATION = "conversationUuid"
        const val EXTRA_DOWNLOAD_UUID = "eu.siacs.conversations.download_uuid"
        const val EXTRA_AS_QUOTE = "eu.siacs.conversations.as_quote"
        const val EXTRA_NICK = "nick"
        const val EXTRA_IS_PRIVATE_MESSAGE = "pm"
        const val EXTRA_DO_NOT_APPEND = "do_not_append"
        const val EXTRA_POST_INIT_ACTION = "post_init_action"
        const val POST_ACTION_RECORD_VOICE = "record_voice"
        const val EXTRA_TYPE = "type"
        private val VIEW_AND_SHARE_ACTIONS = listOf(
            ACTION_VIEW_CONVERSATION,
            Intent.ACTION_SEND,
            Intent.ACTION_SEND_MULTIPLE
        )
        const val REQUEST_OPEN_MESSAGE = 0x9876
        const val REQUEST_PLAY_PAUSE = 0x5432

        private fun isViewOrShareIntent(i: Intent?): Boolean {
            Timber.d("action: " + i?.action)
            return i != null && VIEW_AND_SHARE_ACTIONS.contains(i.action) && i.hasExtra(EXTRA_CONVERSATION)
        }

        private fun createLauncherIntent(context: Context): Intent {
            val intent = Intent(context, ChatsActivity::class.java)
            intent.action = Intent.ACTION_MAIN
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            return intent
        }

        private fun executePendingTransactions(fragmentManager: FragmentManager) {
            try {
                fragmentManager.executePendingTransactions()
            } catch (e: Exception) {
                Timber.e("unable to execute pending fragment transactions")
            }
        }
    }
}