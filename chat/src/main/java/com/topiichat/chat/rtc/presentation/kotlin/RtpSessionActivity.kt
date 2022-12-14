package com.topiichat.chat.rtc.presentation.kotlin

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.PictureInPictureParams
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.os.SystemClock
import android.util.Rational
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import com.google.common.base.Optional
import com.google.common.base.Preconditions
import com.google.common.base.Throwables
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableSet
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import com.topiichat.chat.activity.ChatsActivity
import com.yourbestigor.chat.R
import com.yourbestigor.chat.databinding.ActivityRtpSessionBinding
import eu.siacs.conversations.Config
import eu.siacs.conversations.entities.Account
import eu.siacs.conversations.entities.Contact
import eu.siacs.conversations.services.AppRTCAudioManager
import eu.siacs.conversations.services.AppRTCAudioManager.AudioDevice
import eu.siacs.conversations.services.XmppConnectionService.OnJingleRtpConnectionUpdate
import eu.siacs.conversations.ui.XmppActivity
import eu.siacs.conversations.ui.util.AvatarWorkerTask
import eu.siacs.conversations.ui.util.MainThreadExecutor
import eu.siacs.conversations.ui.util.Rationals
import eu.siacs.conversations.ui.widget.SurfaceViewRenderer.OnAspectRatioChanged
import eu.siacs.conversations.utils.PermissionUtils
import eu.siacs.conversations.utils.TimeFrameUtils
import eu.siacs.conversations.xml.Namespace
import eu.siacs.conversations.xmpp.Jid
import eu.siacs.conversations.xmpp.jingle.JingleConnectionManager.TerminatedRtpSession
import eu.siacs.conversations.xmpp.jingle.JingleRtpConnection
import eu.siacs.conversations.xmpp.jingle.Media
import eu.siacs.conversations.xmpp.jingle.RtpEndUserState
import org.webrtc.RendererCommon
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoTrack
import timber.log.Timber
import java.lang.ref.WeakReference

class RtpSessionActivity : XmppActivity(), OnJingleRtpConnectionUpdate, OnAspectRatioChanged {
    private var rtpConnectionReference: WeakReference<JingleRtpConnection?>? = null
    private lateinit var binding: ActivityRtpSessionBinding
    private var mProximityWakeLock: WakeLock? = null
    private val mHandler = Handler(Looper.getMainLooper())
    private val mTickExecutor: Runnable = object : Runnable {
        override fun run() {
            updateCallDuration()
            mHandler.postDelayed(this, CALL_DURATION_UPDATE_INTERVAL.toLong())
        }
    }

    @Suppress("DEPRECATION")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window
            .addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rtp_session)
        setSupportActionBar(binding.toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_rtp_session, menu)
        val help = menu.findItem(R.id.action_help)
        val gotoChat = menu.findItem(R.id.action_goto_chat)
        help.isVisible = isHelpButtonVisible
        gotoChat.isVisible = isSwitchToConversationVisible
        return super.onCreateOptionsMenu(menu)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (xmppConnectionService != null) {
                if (xmppConnectionService.notificationService.stopSoundAndVibration()) {
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private val isHelpButtonVisible: Boolean
        get() = try {
            STATES_SHOWING_HELP_BUTTON.contains(requireRtpConnection().endUserState)
        } catch (e: IllegalStateException) {
            val intent = intent
            val state = intent?.getStringExtra(EXTRA_LAST_REPORTED_STATE)
            if (state != null) {
                STATES_SHOWING_HELP_BUTTON.contains(RtpEndUserState.valueOf(state))
            } else {
                false
            }
        }
    private val isSwitchToConversationVisible: Boolean
        get() {
            val connection = if (rtpConnectionReference != null) rtpConnectionReference!!.get() else null
            return (connection != null
                && STATES_SHOWING_SWITCH_TO_CHAT.contains(connection.endUserState))
        }

    private fun switchToConversation() {
        val contact = getWith()
        val conversation = xmppConnectionService.findOrCreateConversation(
            contact.account, contact.jid, false, true
        )
        switchToConversation(conversation)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == R.id.action_help) {
            launchHelpInBrowser()
        } else if (itemId == R.id.action_goto_chat) {
            switchToConversation()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun launchHelpInBrowser() {
        val intent = Intent(Intent.ACTION_VIEW, Config.HELP)
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, R.string.no_application_found_to_open_link, Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun endCall() {
        if (rtpConnectionReference == null) {
            retractSessionProposal()
            finish()
        } else {
            requireRtpConnection().endCall()
        }
    }

    private fun retractSessionProposal() {
        val intent = intent
        val action = intent.action
        val account = extractAccount(intent)
        val with = Jid.ofEscaped(intent.getStringExtra(EXTRA_WITH))
        val state = intent.getStringExtra(EXTRA_LAST_REPORTED_STATE)
        if (Intent.ACTION_VIEW != action
            || state == null || !END_CARD.contains(RtpEndUserState.valueOf(state))
        ) {
            resetIntent(
                account, with, RtpEndUserState.RETRACTED, actionToMedia(intent.action)
            )
        }
        xmppConnectionService
            .jingleConnectionManager
            .retractSessionProposal(account, with.asBareJid())
    }

    private fun rejectCall() {
        requireRtpConnection().rejectCall()
        finish()
    }

    private fun acceptCall() {
        requestPermissionsAndAcceptCall()
    }

    private fun requestPermissionsAndAcceptCall() {
        val permissions = ImmutableList.builder<String>()
        if (media.contains(Media.VIDEO)) {
            permissions.add(Manifest.permission.CAMERA).add(Manifest.permission.RECORD_AUDIO)
        } else {
            permissions.add(Manifest.permission.RECORD_AUDIO)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        }
        if (PermissionUtils.hasPermission(this, permissions.build(), REQUEST_ACCEPT_CALL)) {
            putScreenInCallMode()
            checkRecorderAndAcceptCall()
        }
    }

    private fun checkRecorderAndAcceptCall() {
        checkMicrophoneAvailabilityAsync()
        try {
            requireRtpConnection().acceptCall()
        } catch (e: IllegalStateException) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkMicrophoneAvailabilityAsync() {
        Thread(MicrophoneAvailabilityCheck(this)).start()
    }

    private class MicrophoneAvailabilityCheck(activity: Activity) : Runnable {
        private val activityReference: WeakReference<Activity>
        override fun run() {
            val start = SystemClock.elapsedRealtime()
            val isMicrophoneAvailable = AppRTCAudioManager.isMicrophoneAvailable()
            val stop = SystemClock.elapsedRealtime()
            Timber.d("checking microphone availability took " + (stop - start) + "ms")
            if (isMicrophoneAvailable) {
                return
            }
            val activity = activityReference.get() ?: return
            activity.runOnUiThread {
                Toast.makeText(
                    activity,
                    R.string.microphone_unavailable,
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        }

        init {
            activityReference = WeakReference(activity)
        }
    }

    private fun putScreenInCallMode(media: Set<Media> = requireRtpConnection().media) {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        if (!media.contains(Media.VIDEO)) {
            val rtpConnection = if (rtpConnectionReference != null) rtpConnectionReference!!.get() else null
            val audioManager = rtpConnection?.audioManager
            if (audioManager == null
                || audioManager.selectedAudioDevice
                == AudioDevice.EARPIECE
            ) {
                acquireProximityWakeLock()
            }
        }
    }

    @SuppressLint("WakelockTimeout") private fun acquireProximityWakeLock() {
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager?
        if (powerManager == null) {
            Timber.e("power manager not available")
            return
        }
        if (isFinishing) {
            Timber.e("do not acquire wakelock. activity is finishing")
            return
        }
        if (mProximityWakeLock == null) {
            mProximityWakeLock = powerManager.newWakeLock(
                PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, PROXIMITY_WAKE_LOCK_TAG
            )
        }
        if (!mProximityWakeLock!!.isHeld) {
            Timber.d("acquiring proximity wake lock")
            mProximityWakeLock!!.acquire()
        }
    }

    private fun releaseProximityWakeLock() {
        if (mProximityWakeLock != null && mProximityWakeLock!!.isHeld) {
            Timber.d("releasing proximity wake lock")
            mProximityWakeLock!!.release(PowerManager.RELEASE_FLAG_WAIT_FOR_NO_PROXIMITY)
            mProximityWakeLock = null
        }
    }

    private fun putProximityWakeLockInProperState(
        audioDevice: AudioDevice
    ) {
        if (audioDevice == AudioDevice.EARPIECE) {
            acquireProximityWakeLock()
        } else {
            releaseProximityWakeLock()
        }
    }

    override fun refreshUiReal() {}
    public override fun onNewIntent(intent: Intent) {
        Timber.d("%s.onNewIntent()", this.javaClass.name)
        super.onNewIntent(intent)
        setIntent(intent)
        if (xmppConnectionService == null) {
            Timber.d("RtpSessionActivity: background service wasn't bound in onNewIntent()")
            return
        }
        val account = extractAccount(intent)
        val action = intent.action
        val with = Jid.ofEscaped(intent.getStringExtra(EXTRA_WITH))
        val sessionId = intent.getStringExtra(EXTRA_SESSION_ID)
        if (sessionId != null) {
            Timber.d("reinitializing from onNewIntent()")
            if (initializeActivityWithRunningRtpSession(account, with, sessionId)) {
                return
            }
            if (ACTION_ACCEPT_CALL == intent.action) {
                Timber.d("accepting call from onNewIntent()")
                requestPermissionsAndAcceptCall()
                resetIntent(intent.extras)
            }
        } else if (listOf(ACTION_MAKE_VIDEO_CALL, ACTION_MAKE_VOICE_CALL).contains(action)) {
            proposeJingleRtpSession(account, with, actionToMedia(action))
            setWith(account.roster.getContact(with), null)
        } else {
            throw IllegalStateException("received onNewIntent without sessionId")
        }
    }

    override fun onBackendConnected() {
        val intent = intent
        val action = intent.action
        val account = extractAccount(intent)
        val with = Jid.ofEscaped(intent.getStringExtra(EXTRA_WITH))
        val sessionId = intent.getStringExtra(EXTRA_SESSION_ID)
        if (sessionId != null) {
            if (initializeActivityWithRunningRtpSession(account, with, sessionId)) {
                return
            }
            if (ACTION_ACCEPT_CALL == intent.action) {
                Timber.d("intent action was accept")
                requestPermissionsAndAcceptCall()
                resetIntent(intent.extras)
            }
        } else if (listOf(ACTION_MAKE_VIDEO_CALL, ACTION_MAKE_VOICE_CALL).contains(action)) {
            proposeJingleRtpSession(account, with, actionToMedia(action))
            setWith(account.roster.getContact(with), null)
        } else if (Intent.ACTION_VIEW == action) {
            val extraLastState = intent.getStringExtra(EXTRA_LAST_REPORTED_STATE)
            val state = if (extraLastState == null) null else RtpEndUserState.valueOf(extraLastState)
            if (state != null) {
                Timber.d("restored last state from intent extra")
                updateButtonConfiguration(state)
                updateVerifiedShield(false)
                updateStateDisplay(state)
                updateIncomingCallScreen(state)
                invalidateOptionsMenu()
            }
            setWith(account.roster.getContact(with), state)
            if (xmppConnectionService
                    .jingleConnectionManager
                    .fireJingleRtpConnectionStateUpdates()
            ) {
                return
            }
            if (END_CARD.contains(state)
                || xmppConnectionService
                    .jingleConnectionManager
                    .hasMatchingProposal(account, with)
            ) {
                return
            }
            Timber.d("restored state ($state) was not an end card. finishing")
            finish()
        }
    }

    private fun setWidth(state: RtpEndUserState) {
        setWith(getWith(), state)
    }

    private fun setWith(contact: Contact, state: RtpEndUserState?) {
        binding.with.text = contact.displayName
        if (listOf(RtpEndUserState.INCOMING_CALL, RtpEndUserState.ACCEPTING_CALL)
                .contains(state)
        ) {
            binding.withJid.text = contact.jid.asBareJid().toEscapedString()
            binding.withJid.visibility = View.VISIBLE
        } else {
            binding.withJid.visibility = View.GONE
        }
    }

    private fun proposeJingleRtpSession(
        account: Account, with: Jid, media: Set<Media>
    ) {
        checkMicrophoneAvailabilityAsync()
        if (with.isBareJid) {
            xmppConnectionService
                .jingleConnectionManager
                .proposeJingleRtpSession(account, with, media)
        } else {
            val sessionId = xmppConnectionService
                .jingleConnectionManager
                .initializeRtpSession(account, with, media)
            initializeActivityWithRunningRtpSession(account, with, sessionId)
            resetIntent(account, with, sessionId)
        }
        putScreenInCallMode(media)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val permissionResult = PermissionUtils.removeBluetoothConnect(permissions, grantResults)
        if (PermissionUtils.allGranted(permissionResult.grantResults)) {
            if (requestCode == REQUEST_ACCEPT_CALL) {
                checkRecorderAndAcceptCall()
            }
        } else {
            @StringRes val res: Int
            val firstDenied =
                PermissionUtils.getFirstDenied(permissionResult.grantResults, permissionResult.permissions)
                    ?: return
            res = if (Manifest.permission.RECORD_AUDIO == firstDenied) {
                R.string.no_microphone_permission
            } else if (Manifest.permission.CAMERA == firstDenied) {
                R.string.no_camera_permission
            } else {
                throw IllegalStateException("Invalid permission result request")
            }
            Toast.makeText(this, getString(res, getString(R.string.app_name)), Toast.LENGTH_SHORT)
                .show()
        }
    }

    public override fun onStart() {
        super.onStart()
        mHandler.postDelayed(mTickExecutor, CALL_DURATION_UPDATE_INTERVAL.toLong())
        binding.remoteVideo.setOnAspectRatioChanged(this)
    }

    public override fun onStop() {
        mHandler.removeCallbacks(mTickExecutor)
        binding.remoteVideo.release()
        binding.remoteVideo.setOnAspectRatioChanged(null)
        binding.localVideo.release()
        val weakReference = rtpConnectionReference
        val jingleRtpConnection = weakReference?.get()
        jingleRtpConnection?.let { releaseVideoTracks(it) }
        releaseProximityWakeLock()
        super.onStop()
    }

    private fun releaseVideoTracks(jingleRtpConnection: JingleRtpConnection) {
        val remoteVideo = jingleRtpConnection.remoteVideoTrack
        if (remoteVideo.isPresent) {
            remoteVideo.get().removeSink(binding.remoteVideo)
        }
        val localVideo = jingleRtpConnection.localVideoTrack
        if (localVideo.isPresent) {
            localVideo.get().removeSink(binding.localVideo)
        }
    }

    override fun onBackPressed() {
        if (isConnected) {
            if (switchToPictureInPicture()) {
                return
            }
        } else {
            endCall()
        }
        super.onBackPressed()
    }

    public override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (switchToPictureInPicture()) {
            return
        }
        // TODO apparently this method is not getting called on Android 10 when using the task
        // switcher
        if (emptyReference(rtpConnectionReference) && xmppConnectionService != null) {
            retractSessionProposal()
        }
    }

    private val isConnected: Boolean
        get() {
            val connection = if (rtpConnectionReference != null) rtpConnectionReference!!.get() else null
            return (connection != null
                && STATES_CONSIDERED_CONNECTED.contains(connection.endUserState))
        }

    private fun switchToPictureInPicture(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && deviceSupportsPictureInPicture()) {
            if (shouldBePictureInPicture()) {
                startPictureInPicture()
                return true
            }
        }
        return false
    }

    @RequiresApi(api = Build.VERSION_CODES.O) private fun startPictureInPicture() {
        try {
            val rational = binding.remoteVideo.aspectRatio
            val clippedRational = Rationals.clip(rational)
            Timber.d("suggested rational $rational. clipped to $clippedRational")
            enterPictureInPictureMode(
                PictureInPictureParams.Builder().setAspectRatio(clippedRational).build()
            )
        } catch (e: IllegalStateException) {
            // this sometimes happens on Samsung phones (possibly when Knox is enabled)
            Timber.w(e, "unable to enter picture in picture mode")
        }
    }

    override fun onAspectRatioChanged(rational: Rational) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && isPictureInPicture) {
            val clippedRational = Rationals.clip(rational)
            Timber.d(
                "suggested rational after aspect ratio change "
                    + rational
                    + ". clipped to "
                    + clippedRational
            )
            setPictureInPictureParams(
                PictureInPictureParams.Builder().setAspectRatio(clippedRational).build()
            )
        }
    }

    private fun deviceSupportsPictureInPicture(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
        } else {
            false
        }
    }

    private fun shouldBePictureInPicture(): Boolean {
        return try {
            val rtpConnection = requireRtpConnection()
            (rtpConnection.media.contains(Media.VIDEO)
                && listOf(
                RtpEndUserState.ACCEPTING_CALL,
                RtpEndUserState.CONNECTING,
                RtpEndUserState.CONNECTED
            )
                .contains(rtpConnection.endUserState))
        } catch (e: IllegalStateException) {
            false
        }
    }

    private fun initializeActivityWithRunningRtpSession(
        account: Account, with: Jid, sessionId: String
    ): Boolean {
        val reference = xmppConnectionService
            .jingleConnectionManager
            .findJingleRtpConnection(account, with, sessionId)
        if (reference?.get() == null) {
            val terminatedRtpSession = xmppConnectionService
                .jingleConnectionManager
                .getTerminalSessionState(with, sessionId)
                ?: throw IllegalStateException(
                    "failed to initialize activity with running rtp session. session not found"
                )
            initializeWithTerminatedSessionState(account, with, terminatedRtpSession)
            return true
        }
        rtpConnectionReference = reference
        val currentState = requireRtpConnection().endUserState
        val verified = requireRtpConnection().isVerified
        if (currentState == RtpEndUserState.ENDED) {
            finish()
            return true
        }
        val media = media
        if (currentState == RtpEndUserState.INCOMING_CALL) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        if (JingleRtpConnection.STATES_SHOWING_ONGOING_CALL.contains(
                requireRtpConnection().state
            )
        ) {
            putScreenInCallMode()
        }
        setWidth(currentState)
        updateVideoViews(currentState)
        updateStateDisplay(currentState, media)
        updateVerifiedShield(verified && STATES_SHOWING_SWITCH_TO_CHAT.contains(currentState))
        updateButtonConfiguration(currentState, media)
        updateIncomingCallScreen(currentState)
        invalidateOptionsMenu()
        return false
    }

    private fun initializeWithTerminatedSessionState(
        account: Account,
        with: Jid,
        terminatedRtpSession: TerminatedRtpSession
    ) {
        Timber.d("initializeWithTerminatedSessionState()")
        if (terminatedRtpSession.state == RtpEndUserState.ENDED) {
            finish()
            return
        }
        val state = terminatedRtpSession.state
        resetIntent(account, with, terminatedRtpSession.state, terminatedRtpSession.media)
        updateButtonConfiguration(state)
        updateStateDisplay(state)
        updateIncomingCallScreen(state)
        updateCallDuration()
        updateVerifiedShield(false)
        invalidateOptionsMenu()
        setWith(account.roster.getContact(with), state)
    }

    private fun reInitializeActivityWithRunningRtpSession(
        account: Account, with: Jid, sessionId: String
    ) {
        runOnUiThread { initializeActivityWithRunningRtpSession(account, with, sessionId) }
        resetIntent(account, with, sessionId)
    }

    private fun resetIntent(account: Account, with: Jid, sessionId: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.putExtra(EXTRA_ACCOUNT, account.jid.toEscapedString())
        intent.putExtra(EXTRA_WITH, with.toEscapedString())
        intent.putExtra(EXTRA_SESSION_ID, sessionId)
        setIntent(intent)
    }

    private fun ensureSurfaceViewRendererIsSetup(surfaceViewRenderer: SurfaceViewRenderer) {
        surfaceViewRenderer.visibility = View.VISIBLE
        try {
            surfaceViewRenderer.init(requireRtpConnection().eglBaseContext, null)
        } catch (e: IllegalStateException) {
            Timber.d("SurfaceViewRenderer was already initialized")
        }
        surfaceViewRenderer.setEnableHardwareScaler(true)
    }

    private fun updateStateDisplay(state: RtpEndUserState, media: Set<Media> = emptySet()) {
        when (state) {
            RtpEndUserState.INCOMING_CALL -> {
                Preconditions.checkArgument(media.isNotEmpty(), "Media must not be empty")
                if (media.contains(Media.VIDEO)) {
                    setTitle(R.string.rtp_state_incoming_video_call)
                } else {
                    setTitle(R.string.rtp_state_incoming_call)
                }
            }
            RtpEndUserState.CONNECTING -> setTitle(R.string.rtp_state_connecting)
            RtpEndUserState.CONNECTED -> setTitle(R.string.rtp_state_connected)
            RtpEndUserState.RECONNECTING -> setTitle(R.string.rtp_state_reconnecting)
            RtpEndUserState.ACCEPTING_CALL -> setTitle(R.string.rtp_state_accepting_call)
            RtpEndUserState.ENDING_CALL -> setTitle(R.string.rtp_state_ending_call)
            RtpEndUserState.FINDING_DEVICE -> setTitle(R.string.rtp_state_finding_device)
            RtpEndUserState.RINGING -> setTitle(R.string.rtp_state_ringing)
            RtpEndUserState.DECLINED_OR_BUSY -> setTitle(R.string.rtp_state_declined_or_busy)
            RtpEndUserState.CONNECTIVITY_ERROR -> setTitle(R.string.rtp_state_connectivity_error)
            RtpEndUserState.CONNECTIVITY_LOST_ERROR -> setTitle(R.string.rtp_state_connectivity_lost_error)
            RtpEndUserState.RETRACTED -> setTitle(R.string.rtp_state_retracted)
            RtpEndUserState.APPLICATION_ERROR -> setTitle(R.string.rtp_state_application_failure)
            RtpEndUserState.SECURITY_ERROR -> setTitle(R.string.rtp_state_security_error)
            RtpEndUserState.ENDED -> throw IllegalStateException(
                "Activity should have called finishAndReleaseWakeLock();"
            )
        }
    }

    private fun updateVerifiedShield(verified: Boolean) {
        if (isPictureInPicture) {
            binding.verified.visibility = View.GONE
            return
        }
        binding.verified.visibility = if (verified) View.VISIBLE else View.GONE
    }

    private fun updateIncomingCallScreen(state: RtpEndUserState, contact: Contact? = null) {
        if (state == RtpEndUserState.INCOMING_CALL || state == RtpEndUserState.ACCEPTING_CALL) {
            val show = resources.getBoolean(R.bool.show_avatar_incoming_call)
            if (show) {
                binding.contactPhoto.visibility = View.VISIBLE
                if (contact == null) {
                    AvatarWorkerTask.loadAvatar(
                        getWith(), binding.contactPhoto, R.dimen.publish_avatar_size
                    )
                } else {
                    AvatarWorkerTask.loadAvatar(
                        contact, binding.contactPhoto, R.dimen.publish_avatar_size
                    )
                }
            } else {
                binding.contactPhoto.visibility = View.GONE
            }
            val account = if (contact == null) getWith().account else contact.account
            binding.usingAccount.visibility = View.VISIBLE
            binding.usingAccount.text = getString(
                R.string.using_account,
                account.jid.asBareJid().toEscapedString()
            )
        } else {
            binding.usingAccount.visibility = View.GONE
            binding.contactPhoto.visibility = View.GONE
        }
    }

    private val media: Set<Media>
        get() = requireRtpConnection().media

    private fun updateButtonConfiguration(state: RtpEndUserState) {
        updateButtonConfiguration(state, emptySet())
    }

    @SuppressLint("RestrictedApi") private fun updateButtonConfiguration(state: RtpEndUserState, media: Set<Media>) {
        if (state == RtpEndUserState.ENDING_CALL || isPictureInPicture) {
            binding.rejectCall.visibility = View.INVISIBLE
            binding.endCall.visibility = View.INVISIBLE
            binding.acceptCall.visibility = View.INVISIBLE
        } else if (state == RtpEndUserState.INCOMING_CALL) {
            binding.rejectCall.contentDescription = getString(R.string.dismiss_call)
            binding.rejectCall.setOnClickListener { rejectCall() }
            binding.rejectCall.setImageResource(R.drawable.ic_call_end_white_48dp)
            binding.rejectCall.visibility = View.VISIBLE
            binding.endCall.visibility = View.INVISIBLE
            binding.acceptCall.contentDescription = getString(R.string.answer_call)
            binding.acceptCall.setOnClickListener { acceptCall() }
            binding.acceptCall.setImageResource(R.drawable.ic_call_white_48dp)
            binding.acceptCall.visibility = View.VISIBLE
        } else if (state == RtpEndUserState.DECLINED_OR_BUSY) {
            binding.rejectCall.contentDescription = getString(R.string.exit)
            binding.rejectCall.setOnClickListener { exit() }
            binding.rejectCall.setImageResource(R.drawable.ic_clear_white_48dp)
            binding.rejectCall.visibility = View.VISIBLE
            binding.endCall.visibility = View.INVISIBLE
            binding.acceptCall.contentDescription = getString(R.string.record_voice_mail)
            binding.acceptCall.setOnClickListener { recordVoiceMail() }
            binding.acceptCall.setImageResource(R.drawable.ic_voicemail_white_24dp)
            binding.acceptCall.visibility = View.VISIBLE
        } else if (listOf(
                RtpEndUserState.CONNECTIVITY_ERROR,
                RtpEndUserState.CONNECTIVITY_LOST_ERROR,
                RtpEndUserState.APPLICATION_ERROR,
                RtpEndUserState.RETRACTED,
                RtpEndUserState.SECURITY_ERROR
            )
                .contains(state)
        ) {
            binding.rejectCall.contentDescription = getString(R.string.exit)
            binding.rejectCall.setOnClickListener { exit() }
            binding.rejectCall.setImageResource(R.drawable.ic_clear_white_48dp)
            binding.rejectCall.visibility = View.VISIBLE
            binding.endCall.visibility = View.INVISIBLE
            binding.acceptCall.contentDescription = getString(R.string.try_again)
            binding.acceptCall.setOnClickListener { retry() }
            binding.acceptCall.setImageResource(R.drawable.ic_replay_white_48dp)
            binding.acceptCall.visibility = View.VISIBLE
        } else {
            binding.rejectCall.visibility = View.INVISIBLE
            binding.endCall.contentDescription = getString(R.string.hang_up)
            binding.endCall.setOnClickListener { endCall() }
            binding.endCall.setImageResource(R.drawable.ic_call_end_white_48dp)
            binding.endCall.visibility = View.VISIBLE
            binding.acceptCall.visibility = View.INVISIBLE
        }
        updateInCallButtonConfiguration(state, media)
    }

    private val isPictureInPicture: Boolean
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            isInPictureInPictureMode
        } else {
            false
        }

    private fun updateInCallButtonConfiguration() {
        updateInCallButtonConfiguration(
            requireRtpConnection().endUserState, requireRtpConnection().media
        )
    }

    @SuppressLint("RestrictedApi") private fun updateInCallButtonConfiguration(
        state: RtpEndUserState, media: Set<Media>
    ) {
        if (STATES_CONSIDERED_CONNECTED.contains(state) && !isPictureInPicture) {
            Preconditions.checkArgument(media.isNotEmpty(), "Media must not be empty")
            if (media.contains(Media.VIDEO)) {
                val rtpConnection = requireRtpConnection()
                updateInCallButtonConfigurationVideo(
                    rtpConnection.isVideoEnabled, rtpConnection.isCameraSwitchable
                )
            } else {
                val audioManager = requireRtpConnection().audioManager
                updateInCallButtonConfigurationSpeaker(
                    audioManager.selectedAudioDevice,
                    audioManager.audioDevices.size
                )
                binding.inCallActionFarRight.visibility = View.GONE
            }
            if (media.contains(Media.AUDIO)) {
                updateInCallButtonConfigurationMicrophone(
                    requireRtpConnection().isMicrophoneEnabled
                )
            } else {
                binding.inCallActionLeft.visibility = View.GONE
            }
        } else {
            binding.inCallActionLeft.visibility = View.GONE
            binding.inCallActionRight.visibility = View.GONE
            binding.inCallActionFarRight.visibility = View.GONE
        }
    }

    @SuppressLint("RestrictedApi") private fun updateInCallButtonConfigurationSpeaker(
        selectedAudioDevice: AudioDevice, numberOfChoices: Int
    ) {
        when (selectedAudioDevice) {
            AudioDevice.EARPIECE -> {
                binding.inCallActionRight.setImageResource(
                    R.drawable.ic_volume_off_black_24dp
                )
                if (numberOfChoices >= 2) {
                    binding.inCallActionRight.setOnClickListener { switchToSpeaker() }
                } else {
                    binding.inCallActionRight.setOnClickListener(null)
                    binding.inCallActionRight.isClickable = false
                }
            }
            AudioDevice.WIRED_HEADSET -> {
                binding.inCallActionRight.setImageResource(R.drawable.ic_headset_black_24dp)
                binding.inCallActionRight.setOnClickListener(null)
                binding.inCallActionRight.isClickable = false
            }
            AudioDevice.SPEAKER_PHONE -> {
                binding.inCallActionRight.setImageResource(R.drawable.ic_volume_up_black_24dp)
                if (numberOfChoices >= 2) {
                    binding.inCallActionRight.setOnClickListener { switchToEarpiece() }
                } else {
                    binding.inCallActionRight.setOnClickListener(null)
                    binding.inCallActionRight.isClickable = false
                }
            }
            AudioDevice.BLUETOOTH -> {
                binding.inCallActionRight.setImageResource(
                    R.drawable.ic_bluetooth_audio_black_24dp
                )
                binding.inCallActionRight.setOnClickListener(null)
                binding.inCallActionRight.isClickable = false
            }
            else -> {
                //Ignore
            }
        }
        binding.inCallActionRight.visibility = View.VISIBLE
    }

    @SuppressLint("RestrictedApi") private fun updateInCallButtonConfigurationVideo(
        videoEnabled: Boolean, isCameraSwitchable: Boolean
    ) {
        binding.inCallActionRight.visibility = View.VISIBLE
        if (isCameraSwitchable) {
            binding.inCallActionFarRight.setImageResource(
                R.drawable.ic_flip_camera_android_black_24dp
            )
            binding.inCallActionFarRight.visibility = View.VISIBLE
            binding.inCallActionFarRight.setOnClickListener { switchCamera() }
        } else {
            binding.inCallActionFarRight.visibility = View.GONE
        }
        if (videoEnabled) {
            binding.inCallActionRight.setImageResource(R.drawable.ic_videocam_black_24dp)
            binding.inCallActionRight.setOnClickListener { disableVideo() }
        } else {
            binding.inCallActionRight.setImageResource(R.drawable.ic_videocam_off_black_24dp)
            binding.inCallActionRight.setOnClickListener { enableVideo() }
        }
    }

    private fun switchCamera() {
        Futures.addCallback(
            requireRtpConnection().switchCamera(),
            object : FutureCallback<Boolean?> {
                override fun onSuccess(isFrontCamera: Boolean?) {
                    binding.localVideo.setMirror(java.lang.Boolean.TRUE == isFrontCamera)
                }

                override fun onFailure(throwable: Throwable) {
                    Timber.d(Throwables.getRootCause(throwable), "could not switch camera")
                    Toast.makeText(
                        this@RtpSessionActivity,
                        R.string.could_not_switch_camera,
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            },
            MainThreadExecutor.getInstance()
        )
    }

    private fun enableVideo() {
        try {
            requireRtpConnection().isVideoEnabled = true
        } catch (e: IllegalStateException) {
            Toast.makeText(this, R.string.unable_to_enable_video, Toast.LENGTH_SHORT).show()
            return
        }
        updateInCallButtonConfigurationVideo(true, requireRtpConnection().isCameraSwitchable)
    }

    private fun disableVideo() {
        requireRtpConnection().isVideoEnabled = false
        updateInCallButtonConfigurationVideo(false, requireRtpConnection().isCameraSwitchable)
    }

    @SuppressLint("RestrictedApi") private fun updateInCallButtonConfigurationMicrophone(microphoneEnabled: Boolean) {
        if (microphoneEnabled) {
            binding.inCallActionLeft.setImageResource(R.drawable.ic_mic_black_24dp)
            binding.inCallActionLeft.setOnClickListener { disableMicrophone() }
        } else {
            binding.inCallActionLeft.setImageResource(R.drawable.ic_mic_off_black_24dp)
            binding.inCallActionLeft.setOnClickListener { enableMicrophone() }
        }
        binding.inCallActionLeft.visibility = View.VISIBLE
    }

    private fun updateCallDuration() {
        val connection = if (rtpConnectionReference != null) rtpConnectionReference!!.get() else null
        if (connection == null || connection.media.contains(Media.VIDEO)) {
            binding.duration.visibility = View.GONE
            return
        }
        if (connection.zeroDuration()) {
            binding.duration.visibility = View.GONE
        } else {
            binding.duration.text = TimeFrameUtils.formatElapsedTime(connection.callDuration, false)
            binding.duration.visibility = View.VISIBLE
        }
    }

    @Suppress("DEPRECATION")
    private fun updateVideoViews(state: RtpEndUserState) {
        if (END_CARD.contains(state) || state == RtpEndUserState.ENDING_CALL) {
            binding.localVideo.visibility = View.GONE
            binding.localVideo.release()
            binding.remoteVideoWrapper.visibility = View.GONE
            binding.remoteVideo.release()
            binding.pipLocalMicOffIndicator.visibility = View.GONE
            if (isPictureInPicture) {
                binding.appBarLayout.visibility = View.GONE
                binding.pipPlaceholder.visibility = View.VISIBLE
                if (listOf(
                        RtpEndUserState.APPLICATION_ERROR,
                        RtpEndUserState.CONNECTIVITY_ERROR,
                        RtpEndUserState.SECURITY_ERROR
                    )
                        .contains(state)
                ) {
                    binding.pipWarning.visibility = View.VISIBLE
                } else {
                    binding.pipWarning.visibility = View.GONE
                }
                binding.pipWaiting.visibility = View.GONE
            } else {
                binding.appBarLayout.visibility = View.VISIBLE
                binding.pipPlaceholder.visibility = View.GONE
            }
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            return
        }
        if (isPictureInPicture && STATES_SHOWING_PIP_PLACEHOLDER.contains(state)) {
            binding.localVideo.visibility = View.GONE
            binding.remoteVideoWrapper.visibility = View.GONE
            binding.appBarLayout.visibility = View.GONE
            binding.pipPlaceholder.visibility = View.VISIBLE
            binding.pipWarning.visibility = View.GONE
            binding.pipWaiting.visibility = View.VISIBLE
            binding.pipLocalMicOffIndicator.visibility = View.GONE
            return
        }
        val localVideoTrack = localVideoTrack
        if (localVideoTrack.isPresent && !isPictureInPicture) {
            ensureSurfaceViewRendererIsSetup(binding.localVideo)
            // paint local view over remote view
            binding.localVideo.setZOrderMediaOverlay(true)
            binding.localVideo.setMirror(requireRtpConnection().isFrontCamera)
            addSink(localVideoTrack.get(), binding.localVideo)
        } else {
            binding.localVideo.visibility = View.GONE
        }
        val remoteVideoTrack = remoteVideoTrack
        if (remoteVideoTrack.isPresent) {
            ensureSurfaceViewRendererIsSetup(binding.remoteVideo)
            addSink(remoteVideoTrack.get(), binding.remoteVideo)
            binding.remoteVideo.setScalingType(
                RendererCommon.ScalingType.SCALE_ASPECT_FILL,
                RendererCommon.ScalingType.SCALE_ASPECT_FIT
            )
            if (state == RtpEndUserState.CONNECTED) {
                binding.appBarLayout.visibility = View.GONE
                window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                binding.remoteVideoWrapper.visibility = View.VISIBLE
            } else {
                binding.appBarLayout.visibility = View.VISIBLE
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                binding.remoteVideoWrapper.visibility = View.GONE
            }
            if (isPictureInPicture && !requireRtpConnection().isMicrophoneEnabled) {
                binding.pipLocalMicOffIndicator.visibility = View.VISIBLE
            } else {
                binding.pipLocalMicOffIndicator.visibility = View.GONE
            }
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            binding.remoteVideoWrapper.visibility = View.GONE
            binding.pipLocalMicOffIndicator.visibility = View.GONE
        }
    }

    private val localVideoTrack: Optional<VideoTrack>
        get() {
            val connection = (if (rtpConnectionReference != null) rtpConnectionReference!!.get() else null)
                ?: return Optional.absent()
            return connection.localVideoTrack
        }
    private val remoteVideoTrack: Optional<VideoTrack>
        get() {
            val connection = (if (rtpConnectionReference != null) rtpConnectionReference!!.get() else null)
                ?: return Optional.absent()
            return connection.remoteVideoTrack
        }

    private fun disableMicrophone() {
        val rtpConnection = requireRtpConnection()
        if (rtpConnection.setMicrophoneEnabled(false)) {
            updateInCallButtonConfiguration()
        }
    }

    private fun enableMicrophone() {
        val rtpConnection = requireRtpConnection()
        if (rtpConnection.setMicrophoneEnabled(true)) {
            updateInCallButtonConfiguration()
        }
    }

    private fun switchToEarpiece() {
        requireRtpConnection()
            .audioManager
            .setDefaultAudioDevice(AudioDevice.EARPIECE)
        acquireProximityWakeLock()
    }

    private fun switchToSpeaker() {
        requireRtpConnection()
            .audioManager
            .setDefaultAudioDevice(AudioDevice.SPEAKER_PHONE)
        releaseProximityWakeLock()
    }

    private fun retry() {
        val intent = intent
        val account = extractAccount(intent)
        val with = Jid.ofEscaped(intent.getStringExtra(EXTRA_WITH))
        val lastAction = intent.getStringExtra(EXTRA_LAST_ACTION)
        val action = intent.action
        val media = actionToMedia(lastAction ?: action)
        rtpConnectionReference = null
        Timber.d("attempting retry with %s", with.toEscapedString())
        proposeJingleRtpSession(account, with, media)
    }

    private fun exit() {
        finish()
    }

    private fun recordVoiceMail() {
        val intent = intent
        val account = extractAccount(intent)
        val with = Jid.ofEscaped(intent.getStringExtra(EXTRA_WITH))
        val conversation = xmppConnectionService.findOrCreateConversation(account, with, false, true)
        val launchIntent = Intent(this, ChatsActivity::class.java)
        launchIntent.action = ChatsActivity.ACTION_VIEW_CONVERSATION
        launchIntent.putExtra(ChatsActivity.EXTRA_CONVERSATION, conversation.uuid)
        launchIntent.flags = intent.flags or Intent.FLAG_ACTIVITY_CLEAR_TOP
        launchIntent.putExtra(
            ChatsActivity.EXTRA_POST_INIT_ACTION,
            ChatsActivity.POST_ACTION_RECORD_VOICE
        )
        startActivity(launchIntent)
        finish()
    }

    private fun getWith(): Contact {
        val id = requireRtpConnection().id
        val account = id.account
        return account.roster.getContact(id.with)
    }

    private fun requireRtpConnection(): JingleRtpConnection {
        return (if (rtpConnectionReference != null) rtpConnectionReference!!.get() else null)
            ?: throw IllegalStateException("No RTP connection found")
    }

    override fun onJingleRtpConnectionUpdate(
        account: Account, with: Jid, sessionId: String, state: RtpEndUserState
    ) {
        Timber.d("onJingleRtpConnectionUpdate($state)")
        if (END_CARD.contains(state)) {
            Timber.d("end card reached")
            releaseProximityWakeLock()
            runOnUiThread { window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) }
        }
        if (with.isBareJid) {
            updateRtpSessionProposalState(account, with, state)
            return
        }
        if (emptyReference(
                rtpConnectionReference
            )
        ) {
            if (END_CARD.contains(state)) {
                Timber.d("not reinitializing session")
                return
            }
            // this happens when going from proposed session to actual session
            reInitializeActivityWithRunningRtpSession(account, with, sessionId)
            return
        }
        val id = requireRtpConnection().id
        val verified = requireRtpConnection().isVerified
        val media = media
        val contact: Contact = getWith()
        if (account === id.account && id.with == with && id.sessionId == sessionId) {
            if (state == RtpEndUserState.ENDED) {
                finish()
                return
            }
            runOnUiThread {
                updateStateDisplay(state, media)
                updateVerifiedShield(
                    verified && STATES_SHOWING_SWITCH_TO_CHAT.contains(state)
                )
                updateButtonConfiguration(state, media)
                updateVideoViews(state)
                updateIncomingCallScreen(state, contact)
                invalidateOptionsMenu()
            }
            if (END_CARD.contains(state)) {
                val rtpConnection = requireRtpConnection()
                resetIntent(account, with, state, rtpConnection.media)
                releaseVideoTracks(rtpConnection)
                rtpConnectionReference = null
            }
        } else {
            Timber.d("received update for other rtp session")
        }
    }

    override fun onAudioDeviceChanged(
        selectedAudioDevice: AudioDevice,
        availableAudioDevices: Set<AudioDevice>
    ) {
        Timber.d(
            "onAudioDeviceChanged in activity: selected:"
                + selectedAudioDevice
                + ", available:"
                + availableAudioDevices
        )
        try {
            if (media.contains(Media.VIDEO)) {
                Timber.d("nothing to do; in video mode")
                return
            }
            val endUserState = requireRtpConnection().endUserState
            if (endUserState == RtpEndUserState.CONNECTED) {
                val audioManager = requireRtpConnection().audioManager
                updateInCallButtonConfigurationSpeaker(
                    audioManager.selectedAudioDevice,
                    audioManager.audioDevices.size
                )
            } else if (END_CARD.contains(endUserState)) {
                Timber.d("onAudioDeviceChanged() nothing to do because end card has been reached")
            } else {
                putProximityWakeLockInProperState(selectedAudioDevice)
            }
        } catch (e: IllegalStateException) {
            Timber.d("RTP connection was not available when audio device changed")
        }
    }

    private fun updateRtpSessionProposalState(
        account: Account, with: Jid, state: RtpEndUserState
    ) {
        val currentIntent = intent
        val withExtra = currentIntent?.getStringExtra(EXTRA_WITH)
            ?: return
        if (Jid.ofEscaped(withExtra).asBareJid() == with) {
            runOnUiThread {
                updateVerifiedShield(false)
                updateStateDisplay(state)
                updateButtonConfiguration(state)
                updateIncomingCallScreen(state)
                invalidateOptionsMenu()
            }
            resetIntent(
                account, with, state, actionToMedia(
                    currentIntent.action
                )
            )
        }
    }

    private fun resetIntent(extras: Bundle?) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.putExtras(extras!!)
        setIntent(intent)
    }

    private fun resetIntent(
        account: Account, with: Jid, state: RtpEndUserState, media: Set<Media>
    ) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.putExtra(EXTRA_ACCOUNT, account.jid.toEscapedString())
        if (account.roster
                .getContact(with)
                .presences
                .anySupport(Namespace.JINGLE_MESSAGE)
        ) {
            intent.putExtra(EXTRA_WITH, with.asBareJid().toEscapedString())
        } else {
            intent.putExtra(EXTRA_WITH, with.toEscapedString())
        }
        intent.putExtra(EXTRA_LAST_REPORTED_STATE, state.toString())
        intent.putExtra(
            EXTRA_LAST_ACTION,
            if (media.contains(Media.VIDEO)) ACTION_MAKE_VIDEO_CALL else ACTION_MAKE_VOICE_CALL
        )
        setIntent(intent)
    }

    companion object {
        const val EXTRA_WITH = "with"
        const val EXTRA_SESSION_ID = "session_id"
        const val EXTRA_LAST_REPORTED_STATE = "last_reported_state"
        const val EXTRA_LAST_ACTION = "last_action"
        const val ACTION_ACCEPT_CALL = "action_accept_call"
        const val ACTION_MAKE_VOICE_CALL = "action_make_voice_call"
        const val ACTION_MAKE_VIDEO_CALL = "action_make_video_call"
        private const val CALL_DURATION_UPDATE_INTERVAL = 333
        private val END_CARD = listOf(
            RtpEndUserState.APPLICATION_ERROR,
            RtpEndUserState.SECURITY_ERROR,
            RtpEndUserState.DECLINED_OR_BUSY,
            RtpEndUserState.CONNECTIVITY_ERROR,
            RtpEndUserState.CONNECTIVITY_LOST_ERROR,
            RtpEndUserState.RETRACTED
        )
        private val STATES_SHOWING_HELP_BUTTON = listOf(
            RtpEndUserState.APPLICATION_ERROR,
            RtpEndUserState.CONNECTIVITY_ERROR,
            RtpEndUserState.SECURITY_ERROR
        )
        private val STATES_SHOWING_SWITCH_TO_CHAT = listOf(
            RtpEndUserState.CONNECTING,
            RtpEndUserState.CONNECTED,
            RtpEndUserState.RECONNECTING
        )
        private val STATES_CONSIDERED_CONNECTED = listOf(RtpEndUserState.CONNECTED, RtpEndUserState.RECONNECTING)
        private val STATES_SHOWING_PIP_PLACEHOLDER = listOf(
            RtpEndUserState.ACCEPTING_CALL,
            RtpEndUserState.CONNECTING,
            RtpEndUserState.RECONNECTING
        )
        private const val PROXIMITY_WAKE_LOCK_TAG = "conversations:in-rtp-session"
        private const val REQUEST_ACCEPT_CALL = 0x1111
        private fun actionToMedia(action: String?): Set<Media> {
            return if (ACTION_MAKE_VIDEO_CALL == action) {
                ImmutableSet.of(
                    Media.AUDIO,
                    Media.VIDEO
                )
            } else {
                ImmutableSet.of(Media.AUDIO)
            }
        }

        private fun addSink(
            videoTrack: VideoTrack, surfaceViewRenderer: SurfaceViewRenderer
        ) {
            try {
                videoTrack.addSink(surfaceViewRenderer)
            } catch (e: IllegalStateException) {
                Timber.e(e, "possible race condition on trying to display video track. ignoring")
            }
        }

        private fun emptyReference(weakReference: WeakReference<*>?): Boolean {
            return weakReference?.get() == null
        }
    }
}