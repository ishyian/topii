/*
 * Copyright (c) 2018, Daniel Gultsch All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import com.topiichat.app.core.util.AccountUtil.createMockAccount
import com.topiichat.app.features.chats.activity.ChatsActivity
import com.topiichat.app.features.chats.root.presentation.ChatsFragment
import com.yourbestigor.chat.R
import com.yourbestigor.chat.databinding.ActivityConversationsBinding
import dagger.hilt.android.AndroidEntryPoint
import eu.siacs.conversations.Config
import eu.siacs.conversations.crypto.OmemoSetting
import eu.siacs.conversations.entities.Account
import eu.siacs.conversations.entities.Conversation
import eu.siacs.conversations.services.XmppConnectionService.OnAccountUpdate
import eu.siacs.conversations.services.XmppConnectionService.OnAffiliationChanged
import eu.siacs.conversations.services.XmppConnectionService.OnConversationUpdate
import eu.siacs.conversations.services.XmppConnectionService.OnRosterUpdate
import eu.siacs.conversations.services.XmppConnectionService.OnShowErrorToast
import eu.siacs.conversations.ui.ConversationFragment
import eu.siacs.conversations.ui.ConversationsOverviewFragment
import eu.siacs.conversations.ui.EditAccountActivity
import eu.siacs.conversations.ui.SearchActivity
import eu.siacs.conversations.ui.StartConversationActivity
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
import eu.siacs.conversations.utils.AccountUtils
import eu.siacs.conversations.utils.ActionBarUtil
import eu.siacs.conversations.utils.ExceptionHelper
import eu.siacs.conversations.utils.SignupUtils
import eu.siacs.conversations.utils.XmppUri
import eu.siacs.conversations.xmpp.Jid
import eu.siacs.conversations.xmpp.OnUpdateBlocklist
import org.openintents.openpgp.util.OpenPgpApi
import java.util.Arrays
import java.util.concurrent.atomic.AtomicBoolean

@AndroidEntryPoint
class ChatsActivity : XmppActivity(), OnConversationSelected, OnConversationArchived, OnConversationsListItemUpdated,
    OnConversationRead, OnAccountUpdate, OnConversationUpdate, OnRosterUpdate, OnUpdateBlocklist, OnShowErrorToast,
    OnAffiliationChanged {
    private val pendingViewIntent = PendingItem<Intent?>()
    private val postponedActivityResult = PendingItem<ActivityResult>()
    private lateinit var binding: ActivityConversationsBinding
    private var mActivityPaused = true
    private val mRedirectInProcess = AtomicBoolean(false)
    override fun refreshUiReal() {
        invalidateOptionsMenu()
        for (@IdRes id in FRAGMENT_ID_NOTIFICATION_ORDER) {
            refreshFragment(id)
        }
    }

    override fun onBackendConnected() {
        if (performRedirectIfNecessary(true)) {
            return
        }
        xmppConnectionService.notificationService.setIsInForeground(true)
        val intent = pendingViewIntent.pop()
        if (intent != null) {
            if (processViewIntent(intent)) {
                if (binding!!.secondaryFragment != null) {
                    notifyFragmentOfBackendConnected(R.id.main_fragment)
                }
                invalidateActionBarTitle()
                return
            }
        }
        for (@IdRes id in FRAGMENT_ID_NOTIFICATION_ORDER) {
            notifyFragmentOfBackendConnected(id)
        }
        val activityResult = postponedActivityResult.pop()
        activityResult?.let { handleActivityResult(it) }
        invalidateActionBarTitle()
        if (binding!!.secondaryFragment != null && ConversationFragment.getConversation(this) == null) {
            val conversation = ConversationsOverviewFragment.getSuggestion(this)
            if (conversation != null) {
                openConversation(conversation, null)
            }
        }
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
        if (isConversationsListEmpty && mRedirectInProcess.compareAndSet(false, true)) {
            val account = createMockAccount(xmppConnectionService)
            val intent = Intent(this, EditAccountActivity::class.java)
            intent.putExtra("jid", account!!.jid.asBareJid().toString())
            intent.putExtra("init", true)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            Toast.makeText(this, R.string.secure_password_generated, Toast.LENGTH_SHORT).show()
            runOnUiThread {
                startActivity(intent)
                if (noAnimation) {
                    overridePendingTransition(0, 0)
                }
            }
        }
        return mRedirectInProcess.get()
    }

    private fun showDialogsIfMainIsOverview() {
        if (xmppConnectionService == null) {
            return
        }
        val fragment = supportFragmentManager.findFragmentById(R.id.main_fragment)
        if (fragment is ChatsFragment) {
            if (ExceptionHelper.checkForCrash(this)) {
                return
            }
            openBatteryOptimizationDialogIfNeeded()
        }
    }

    private val batteryOptimizationPreferenceKey: String
        private get() {
            @SuppressLint("HardwareIds") val device =
                Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
            return "show_battery_optimization" + (device ?: "")
        }

    private fun setNeverAskForBatteryOptimizationsAgain() {
        preferences.edit().putBoolean(batteryOptimizationPreferenceKey, false).apply()
    }

    private fun openBatteryOptimizationDialogIfNeeded() {
        if (isOptimizingBattery
            && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && preferences.getBoolean(
                batteryOptimizationPreferenceKey,
                true
            )
        ) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.battery_optimizations_enabled)
            builder.setMessage(getString(R.string.battery_optimizations_enabled_dialog, getString(R.string.app_name)))
            builder.setPositiveButton(R.string.next) { dialog: DialogInterface?, which: Int ->
                val intent = Intent(
                    Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                )
                val uri = Uri.parse("package:$packageName")
                intent.data = uri
                try {
                    startActivityForResult(intent, REQUEST_BATTERY_OP)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(this, R.string.device_does_not_support_battery_op, Toast.LENGTH_SHORT).show()
                }
            }
            builder.setOnDismissListener { dialog: DialogInterface? -> setNeverAskForBatteryOptimizationsAgain() }
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
        //TODO
        /*if (fragment instanceof XmppFragment) {
            ((XmppFragment) fragment).refresh();
        }*/
    }

    private fun processViewIntent(intent: Intent): Boolean {
        val uuid = intent.getStringExtra(EXTRA_CONVERSATION)
        val conversation = if (uuid != null) xmppConnectionService.findConversationByUuid(uuid) else null
        if (conversation == null) {
            Log.d(Config.LOGTAG, "unable to view conversation with uuid:$uuid")
            return false
        }
        openConversation(conversation, intent.extras)
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        UriHandlerActivity.onRequestPermissionResult(this, requestCode, grantResults)
        if (grantResults.size > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                when (requestCode) {
                    REQUEST_OPEN_MESSAGE -> {
                        refreshUiReal()
                        ConversationFragment.openPendingMessage(this)
                    }
                    REQUEST_PLAY_PAUSE -> ConversationFragment.startStopPending(this)
                }
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val activityResult = ActivityResult.of(requestCode, resultCode, data)
        if (xmppConnectionService != null) {
            handleActivityResult(activityResult)
        } else {
            postponedActivityResult.push(activityResult)
        }
    }

    private fun handleActivityResult(activityResult: ActivityResult) {
        if (activityResult.resultCode == RESULT_OK) {
            handlePositiveActivityResult(activityResult.requestCode, activityResult.data)
        } else {
            handleNegativeActivityResult(activityResult.requestCode)
        }
    }

    private fun handleNegativeActivityResult(requestCode: Int) {
        val conversation = ConversationFragment.getConversationReliable(this)
        when (requestCode) {
            ConversationFragment.REQUEST_DECRYPT_PGP -> {
                if (conversation == null) {
                    return
                }
                conversation.account.pgpDecryptionService.giveUpCurrentDecryption()
            }
            REQUEST_BATTERY_OP -> setNeverAskForBatteryOptimizationsAgain()
        }
    }

    private fun handlePositiveActivityResult(requestCode: Int, data: Intent) {
        val conversation = ConversationFragment.getConversationReliable(this)
        if (conversation == null) {
            Log.d(Config.LOGTAG, "conversation not found")
            return
        }
        when (requestCode) {
            ConversationFragment.REQUEST_DECRYPT_PGP -> conversation.account.pgpDecryptionService.continueDecryption(
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
        ConversationMenuConfigurator.reloadFeatures(this)
        OmemoSetting.load(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_conversations)
        setSupportActionBar(binding.toolbar)
        configureActionBar(supportActionBar)
        this.fragmentManager.addOnBackStackChangedListener { invalidateActionBarTitle() }
        this.fragmentManager.addOnBackStackChangedListener { showDialogsIfMainIsOverview() }
        initializeFragments()
        invalidateActionBarTitle()
        val intent: Intent?
        intent = if (savedInstanceState == null) {
            getIntent()
        } else {
            savedInstanceState.getParcelable("intent")
        }
        if (isViewOrShareIntent(intent)) {
            pendingViewIntent.push(intent)
            setIntent(createLauncherIntent(this))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_conversations, menu)
        val qrCodeScanMenuItem = menu.findItem(R.id.action_scan_qr_code)
        if (qrCodeScanMenuItem != null) {
            if (isCameraFeatureAvailable) {
                val fragment = supportFragmentManager.findFragmentById(R.id.main_fragment)
                val visible = (resources.getBoolean(R.bool.show_qr_code_scan)
                    && fragment is ChatsFragment)
                qrCodeScanMenuItem.isVisible = visible
            } else {
                qrCodeScanMenuItem.isVisible = false
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onConversationSelected(conversation: Conversation) {
        clearPendingViewIntent()
        if (ConversationFragment.getConversation(this) === conversation) {
            Log.d(Config.LOGTAG, "ignore onConversationSelected() because conversation is already open")
            return
        }
        openConversation(conversation, null)
    }

    fun clearPendingViewIntent() {
        if (pendingViewIntent.clear()) {
            Log.e(Config.LOGTAG, "cleared pending view intent")
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
        /*final FragmentManager fragmentManager = getFragmentManager();
        executePendingTransactions(fragmentManager);
        ConversationFragment conversationFragment = (ConversationFragment) fragmentManager.findFragmentById(R.id.secondary_fragment);
        final boolean mainNeedsRefresh;
        if (conversationFragment == null) {
            mainNeedsRefresh = false;
            final Fragment mainFragment = fragmentManager.findFragmentById(R.id.main_fragment);
            if (mainFragment instanceof ConversationFragment) {
                conversationFragment = (ConversationFragment) mainFragment;
            } else {
                conversationFragment = new ConversationFragment();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_fragment, conversationFragment);
                fragmentTransaction.addToBackStack(null);
                try {
                    fragmentTransaction.commit();
                } catch (IllegalStateException e) {
                    Log.w(Config.LOGTAG, "sate loss while opening conversation", e);
                    //allowing state loss is probably fine since view intents et all are already stored and a click can probably be 'ignored'
                    return;
                }
            }
        } else {
            mainNeedsRefresh = true;
        }
        conversationFragment.reInit(conversation, extras == null ? new Bundle() : extras);
        if (mainNeedsRefresh) {
            refreshFragment(R.id.main_fragment);
        } else {
            invalidateActionBarTitle();
        }*/
    }

    fun onXmppUriClicked(uri: Uri?): Boolean {
        val xmppUri = XmppUri(uri)
        if (xmppUri.isValidJid && !xmppUri.hasFingerprints()) {
            val conversation = xmppConnectionService.findUniqueConversationByJid(xmppUri)
            if (conversation != null) {
                openConversation(conversation, null)
                return true
            }
        }
        return false
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
                    Log.w(Config.LOGTAG, "Unable to pop back stack after pressing home button")
                }
                return true
            }
        } else if (itemId == R.id.action_scan_qr_code) {
            UriHandlerActivity.scan(this)
            return true
        } else if (itemId == R.id.action_search_all_conversations) {
            startActivity(Intent(this, SearchActivity::class.java))
            return true
        } else if (itemId == R.id.action_search_this_conversation) {
            val conversation = ConversationFragment.getConversation(this) ?: return true
            val intent = Intent(this, SearchActivity::class.java)
            intent.putExtra(SearchActivity.EXTRA_CONVERSATION_UUID, conversation.uuid)
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onKeyDown(keyCode: Int, keyEvent: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP && keyEvent.isCtrlPressed) {
            val conversationFragment = ConversationFragment.get(this)
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
        val theme = findTheme()
        if (mTheme != theme) {
            mSkipBackgroundBinding = true
            recreate()
        } else {
            mSkipBackgroundBinding = false
        }
        mRedirectInProcess.set(false)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (isViewOrShareIntent(intent)) {
            if (xmppConnectionService != null) {
                clearPendingViewIntent()
                processViewIntent(intent)
            } else {
                pendingViewIntent.push(intent)
            }
        }
        setIntent(createLauncherIntent(this))
    }

    override fun onPause() {
        mActivityPaused = true
        super.onPause()
    }

    public override fun onResume() {
        super.onResume()
        mActivityPaused = false
    }

    private fun initializeFragments() {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        val mainFragment = fragmentManager.findFragmentById(R.id.main_fragment)
        val secondaryFragment = fragmentManager.findFragmentById(R.id.secondary_fragment)
        if (mainFragment != null) {
            /*if (binding.secondaryFragment != null) {
                if (mainFragment instanceof ConversationFragment) {
                    getFragmentManager().popBackStack();
                    transaction.remove(mainFragment);
                    transaction.commit();
                    fragmentManager.executePendingTransactions();
                    transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.secondary_fragment, mainFragment);
                    transaction.replace(R.id.main_fragment, new ChatsFragment());
                    transaction.commit();
                    return;
                }
            } else {
                if (secondaryFragment instanceof ConversationFragment) {
                    transaction.remove(secondaryFragment);
                    transaction.commit();
                    getFragmentManager().executePendingTransactions();
                    transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.main_fragment, secondaryFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    return;
                }
            }*/
        } else {
            transaction.replace(R.id.main_fragment, ChatsFragment())
        }
        /*if (binding.secondaryFragment != null && secondaryFragment == null) {
            transaction.replace(R.id.secondary_fragment, new ConversationFragment());
        }*/transaction.commit()
    }

    private fun invalidateActionBarTitle() {
        val actionBar = supportActionBar ?: return
        val fragmentManager = supportFragmentManager
        val mainFragment = fragmentManager.findFragmentById(R.id.main_fragment)
        if (mainFragment is ChatsFragment) {
            /*final Conversation conversation = ((ChatsFragment) mainFragment).getConversation();
            if (conversation != null) {
                actionBar.setTitle(conversation.getName());
                actionBar.setDisplayHomeAsUpEnabled(true);
                ActionBarUtil.setActionBarOnClickListener(
                        binding.toolbar,
                        (v) -> openConversationDetails(conversation)
                );
                return;
            }*/
        }
        actionBar.setTitle(R.string.app_name)
        actionBar.setDisplayHomeAsUpEnabled(false)
        ActionBarUtil.resetActionBarOnClickListeners(binding!!.toolbar)
    }

    private fun openConversationDetails(conversation: Conversation) {
        /*if (conversation.getMode() == Conversational.MODE_MULTI) {
            ConferenceDetailsActivity.open(this, conversation);
        } else {
            final Contact contact = conversation.getContact();
            if (contact.isSelf()) {
                switchToAccount(conversation.getAccount());
            } else {
                switchToContactDetails(contact);
            }
        }*/
    }

    override fun onConversationArchived(conversation: Conversation) {
        if (performRedirectIfNecessary(conversation, false)) {
            return
        }
        val fragmentManager = supportFragmentManager
        val mainFragment = fragmentManager.findFragmentById(R.id.main_fragment)
        /*if (mainFragment instanceof ConversationFragment) {
            try {
                fragmentManager.popBackStack();
            } catch (final IllegalStateException e) {
                Log.w(Config.LOGTAG, "state loss while popping back state after archiving conversation", e);
                //this usually means activity is no longer active; meaning on the next open we will run through this again
            }
            return;
        }*/
        val secondaryFragment = fragmentManager.findFragmentById(R.id.secondary_fragment)
        /*if (secondaryFragment instanceof ConversationFragment) {
            if (((ConversationFragment) secondaryFragment).getConversation() == conversation) {
                Conversation suggestion = ConversationsOverviewFragment.getSuggestion(this, conversation);
                if (suggestion != null) {
                    openConversation(suggestion, null);
                }
            }
        }*/
    }

    override fun onConversationsListItemUpdated() {
        val fragment = supportFragmentManager.findFragmentById(R.id.main_fragment)
        //TODO
        /*if (fragment instanceof ChatsFragment) {
            ((ChatsFragment) fragment).refresh();
        }*/
    }

    override fun switchToConversation(conversation: Conversation) {
        Log.d(Config.LOGTAG, "override")
        openConversation(conversation, null)
    }

    override fun onConversationRead(conversation: Conversation, upToUuid: String) {
        if (!mActivityPaused && pendingViewIntent.peek() == null) {
            xmppConnectionService.sendReadMarker(conversation, upToUuid)
        } else {
            Log.d(Config.LOGTAG, "ignoring read callback. mActivityPaused=$mActivityPaused")
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
        private val VIEW_AND_SHARE_ACTIONS = Arrays.asList(
            ACTION_VIEW_CONVERSATION,
            Intent.ACTION_SEND,
            Intent.ACTION_SEND_MULTIPLE
        )
        const val REQUEST_OPEN_MESSAGE = 0x9876
        const val REQUEST_PLAY_PAUSE = 0x5432

        //secondary fragment (when holding the conversation, must be initialized before refreshing the overview fragment
        @IdRes
        private val FRAGMENT_ID_NOTIFICATION_ORDER = intArrayOf(R.id.secondary_fragment, R.id.main_fragment)
        private fun isViewOrShareIntent(i: Intent?): Boolean {
            Log.d(Config.LOGTAG, "action: " + i?.action)
            return i != null && VIEW_AND_SHARE_ACTIONS.contains(i.action) && i.hasExtra(EXTRA_CONVERSATION)
        }

        private fun createLauncherIntent(context: Context): Intent {
            val intent = Intent(context, ChatsActivity::class.java)
            intent.action = Intent.ACTION_MAIN
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            return intent
        }

        fun getRedirectionIntent(activity: ChatsActivity): Intent {
            val service = activity.xmppConnectionService
            val pendingAccount = AccountUtils.getPendingAccount(service)
            val intent: Intent
            if (pendingAccount != null) {
                intent = Intent(activity, EditAccountActivity::class.java)
                intent.putExtra("jid", pendingAccount.jid.asBareJid().toString())
                if (!pendingAccount.isOptionSet(Account.OPTION_MAGIC_CREATE)) {
                    intent.putExtra(
                        EditAccountActivity.EXTRA_FORCE_REGISTER,
                        pendingAccount.isOptionSet(Account.OPTION_REGISTER)
                    )
                }
            } else {
                if (service.accounts.size == 0) {
                    if (Config.X509_VERIFICATION) {
                        intent = Intent(activity, EditAccountActivity::class.java)
                        //intent = new Intent(activity, ManageAccountActivity.class);
                    } else if (Config.MAGIC_CREATE_DOMAIN != null) {
                        intent = SignupUtils.getSignUpIntent(activity)
                    } else {
                        intent = Intent(activity, EditAccountActivity::class.java)
                    }
                } else {
                    intent = Intent(activity, StartConversationActivity::class.java)
                }
            }
            intent.putExtra("init", true)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            return intent
        }

        private fun executePendingTransactions(fragmentManager: FragmentManager) {
            try {
                fragmentManager.executePendingTransactions()
            } catch (e: Exception) {
                Log.e(Config.LOGTAG, "unable to execute pending fragment transactions")
            }
        }
    }
}