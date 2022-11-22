package com.topiichat.app.features.chats.chat.adapter

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.view.View
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.topiichat.app.features.chats.activity.ChatsActivity
import com.yourbestigor.chat.R
import eu.siacs.conversations.entities.Message
import eu.siacs.conversations.ui.util.PendingItem
import eu.siacs.conversations.utils.WeakReferenceSet
import timber.log.Timber
import java.lang.ref.WeakReference
import java.util.Locale
import java.util.concurrent.Executors
import kotlin.math.min
import kotlin.math.roundToInt

class AudioPlayer(adapter: MessageAdapter) : View.OnClickListener, OnCompletionListener, OnSeekBarChangeListener,
    Runnable, SensorEventListener {
    private val messageAdapter: MessageAdapter
    private val audioPlayerLayouts = WeakReferenceSet<RelativeLayout>()
    private val sensorManager: SensorManager?
    private val proximitySensor: Sensor?
    private val pendingOnClickView = PendingItem<WeakReference<ImageButton>>()
    private val executor = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())
    private fun initializeProximityWakeLock(context: Context) {
        synchronized(LOCK) {
            if (wakeLock == null) {
                val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager?
                wakeLock = powerManager?.newWakeLock(
                    PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK,
                    AudioPlayer::class.java.simpleName
                )
                wakeLock?.setReferenceCounted(false)
            }
        }
    }

    fun init(audioPlayer: RelativeLayout, message: Message) {
        synchronized(LOCK) {
            audioPlayer.tag = message
            if (init(ViewHolder[audioPlayer], message)) {
                audioPlayerLayouts.addWeakReferenceTo(audioPlayer)
                executor.execute { stopRefresher(true) }
            } else {
                audioPlayerLayouts.removeWeakReferenceTo(audioPlayer)
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun init(viewHolder: ViewHolder, message: Message): Boolean {
        if (viewHolder.darkBackground) {
            viewHolder.runtime?.setTextAppearance(
                messageAdapter.context,
                R.style.TextAppearance_Conversations_Caption_OnDark
            )
        } else {
            viewHolder.runtime?.setTextAppearance(messageAdapter.context, R.style.TextAppearance_Conversations_Caption)
        }
        viewHolder.progress!!.setOnSeekBarChangeListener(this)
        val color = ContextCompat.getColorStateList(
            messageAdapter.context,
            if (viewHolder.darkBackground) R.color.white70 else R.color.green700_desaturated
        )
        viewHolder.progress?.thumbTintList = color
        viewHolder.progress?.progressTintList = color
        viewHolder.playPause?.alpha = if (viewHolder.darkBackground) 0.7f else 0.57f
        viewHolder.playPause?.setOnClickListener(this)
        val context = viewHolder.playPause?.context
        return if (message === currentlyPlayingMessage) {
            if (player != null && player?.isPlaying == true) {
                viewHolder.playPause!!.setImageResource(if (viewHolder.darkBackground) R.drawable.ic_pause_white_36dp else R.drawable.ic_pause_black_36dp)
                viewHolder.playPause!!.contentDescription = context?.getString(R.string.pause_audio)
                viewHolder.progress!!.isEnabled = true
            } else {
                viewHolder.playPause!!.contentDescription = context?.getString(R.string.play_audio)
                viewHolder.playPause!!.setImageResource(if (viewHolder.darkBackground) R.drawable.ic_play_arrow_white_36dp else R.drawable.ic_play_arrow_black_36dp)
                viewHolder.progress!!.isEnabled = false
            }
            true
        } else {
            viewHolder.playPause!!.setImageResource(if (viewHolder.darkBackground) R.drawable.ic_play_arrow_white_36dp else R.drawable.ic_play_arrow_black_36dp)
            viewHolder.playPause!!.contentDescription = context?.getString(R.string.play_audio)
            viewHolder.runtime!!.text =
                formatTime(message.fileParams.runtime)
            viewHolder.progress!!.progress = 0
            viewHolder.progress!!.isEnabled = false
            false
        }
    }

    @Synchronized override fun onClick(v: View) {
        if (v.id == R.id.play_pause) {
            synchronized(LOCK) { startStop(v as ImageButton) }
        }
    }

    private fun startStop(playPause: ImageButton) {
        if (ContextCompat.checkSelfPermission(
                messageAdapter.getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            pendingOnClickView.push(WeakReference(playPause))
            ActivityCompat.requestPermissions(
                messageAdapter.getActivity(),
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                ChatsActivity.REQUEST_PLAY_PAUSE
            )
            return
        }
        initializeProximityWakeLock(playPause.context)
        val audioPlayer = playPause.parent as RelativeLayout
        val viewHolder = ViewHolder[audioPlayer]
        val message = audioPlayer.tag as Message
        if (startStop(viewHolder, message)) {
            audioPlayerLayouts.clear()
            audioPlayerLayouts.addWeakReferenceTo(audioPlayer)
            stopRefresher(true)
        }
    }

    private fun playPauseCurrent(viewHolder: ViewHolder): Boolean {
        val context = viewHolder.playPause!!.context
        viewHolder.playPause!!.alpha = if (viewHolder.darkBackground) 0.7f else 0.57f
        if (player!!.isPlaying) {
            viewHolder.progress!!.isEnabled = false
            player!!.pause()
            messageAdapter.flagScreenOff()
            releaseProximityWakeLock()
            viewHolder.playPause!!.setImageResource(if (viewHolder.darkBackground) R.drawable.ic_play_arrow_white_36dp else R.drawable.ic_play_arrow_black_36dp)
            viewHolder.playPause!!.contentDescription = context.getString(R.string.play_audio)
        } else {
            viewHolder.progress!!.isEnabled = true
            player!!.start()
            messageAdapter.flagScreenOn()
            acquireProximityWakeLock()
            stopRefresher(true)
            viewHolder.playPause!!.setImageResource(if (viewHolder.darkBackground) R.drawable.ic_pause_white_36dp else R.drawable.ic_pause_black_36dp)
            viewHolder.playPause!!.contentDescription = context.getString(R.string.pause_audio)
        }
        return false
    }

    private fun play(viewHolder: ViewHolder, message: Message?, earpiece: Boolean, progress: Double) {
        if (play(viewHolder, message, earpiece)) {
            player!!.seekTo((player!!.duration * progress).toInt())
        }
    }

    private fun play(viewHolder: ViewHolder, message: Message?, earpiece: Boolean): Boolean {
        player = eu.siacs.conversations.services.MediaPlayer()
        return try {
            currentlyPlayingMessage = message
            player?.audioStreamType = if (earpiece) AudioManager.STREAM_VOICE_CALL else AudioManager.STREAM_MUSIC
            player?.setDataSource(messageAdapter.fileBackend.getFile(message).absolutePath)
            player!!.setOnCompletionListener(this)
            player!!.prepare()
            player!!.start()
            messageAdapter.flagScreenOn()
            acquireProximityWakeLock()
            viewHolder.progress!!.isEnabled = true
            viewHolder.playPause!!.setImageResource(if (viewHolder.darkBackground) R.drawable.ic_pause_white_36dp else R.drawable.ic_pause_black_36dp)
            viewHolder.playPause!!.contentDescription = viewHolder.playPause!!.context.getString(R.string.pause_audio)
            sensorManager!!.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL)
            true
        } catch (e: Exception) {
            messageAdapter.flagScreenOff()
            releaseProximityWakeLock()
            currentlyPlayingMessage = null
            sensorManager!!.unregisterListener(this)
            false
        }
    }

    fun startStopPending() {
        val reference = pendingOnClickView.pop()
        if (reference != null) {
            val imageButton = reference.get()
            imageButton?.let { startStop(it) }
        }
    }

    private fun startStop(viewHolder: ViewHolder, message: Message): Boolean {
        if (message === currentlyPlayingMessage && player != null) {
            return playPauseCurrent(viewHolder)
        }
        if (player != null) {
            stopCurrent()
        }
        return play(viewHolder, message, false)
    }

    private fun stopCurrent() {
        if (player?.isPlaying == true) {
            player?.stop()
        }
        player?.release()
        messageAdapter.flagScreenOff()
        releaseProximityWakeLock()
        player = null
        resetPlayerUi()
    }

    private fun resetPlayerUi() {
        for (audioPlayer in audioPlayerLayouts) {
            resetPlayerUi(audioPlayer.get())
        }
    }

    private fun resetPlayerUi(audioPlayer: RelativeLayout?) {
        if (audioPlayer == null) {
            return
        }
        val viewHolder = ViewHolder[audioPlayer]
        val message = audioPlayer.tag as Message?
        viewHolder.playPause!!.contentDescription = viewHolder.playPause!!.context.getString(R.string.play_audio)
        viewHolder.playPause!!.setImageResource(if (viewHolder.darkBackground) R.drawable.ic_play_arrow_white_36dp else R.drawable.ic_play_arrow_black_36dp)
        if (message != null) {
            viewHolder.runtime?.text =
                formatTime(message.fileParams.runtime)
        }
        viewHolder.progress?.progress = 0
        viewHolder.progress?.isEnabled = false
    }

    override fun onCompletion(mediaPlayer: MediaPlayer) {
        synchronized(LOCK) {
            stopRefresher(false)
            if (player === mediaPlayer) {
                currentlyPlayingMessage = null
                player = null
            }
            mediaPlayer.release()
            messageAdapter.flagScreenOff()
            releaseProximityWakeLock()
            resetPlayerUi()
            sensorManager?.unregisterListener(this)
        }
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        synchronized(LOCK) {
            val audioPlayer = seekBar.parent as RelativeLayout
            val message = audioPlayer.tag as Message
            if (fromUser && message === currentlyPlayingMessage) {
                val percent = progress / 100f
                val duration = player?.duration ?: 0
                val seekTo = (duration * percent).roundToInt()
                player?.seekTo(seekTo)
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {}
    override fun onStopTrackingTouch(seekBar: SeekBar) {}
    fun stop() {
        synchronized(LOCK) {
            stopRefresher(false)
            if (player != null) {
                stopCurrent()
            }
            currentlyPlayingMessage = null
            sensorManager?.unregisterListener(this)
            if (wakeLock != null && wakeLock?.isHeld == true) {
                wakeLock?.release()
            }
            wakeLock = null
        }
    }

    private fun stopRefresher(runOnceMore: Boolean) {
        handler.removeCallbacks(this)
        if (runOnceMore) {
            handler.post(this)
        }
    }

    fun unregisterListener() {
        sensorManager?.unregisterListener(this)
    }

    override fun run() {
        synchronized(LOCK) {
            if (player != null) {
                var renew = false
                val current = player?.currentPosition
                val duration = player?.duration
                for (audioPlayer in audioPlayerLayouts) {
                    renew = renew or refreshAudioPlayer(audioPlayer.get(), current ?: 0, duration ?: 0)
                }
                if (renew && player?.isPlaying == true) {
                    handler.postDelayed(this, REFRESH_INTERVAL.toLong())
                }
            }
        }
    }

    private fun refreshAudioPlayer(audioPlayer: RelativeLayout?, current: Int, duration: Int): Boolean {
        if (audioPlayer == null || audioPlayer.visibility != View.VISIBLE) {
            return false
        }
        val viewHolder = ViewHolder[audioPlayer]
        if (duration <= 0) {
            viewHolder.progress?.progress = 100
        } else {
            viewHolder.progress?.progress = current * 100 / duration
        }
        viewHolder.runtime?.text = String.format(
            "%s / %s",
            formatTime(current),
            formatTime(duration)
        )
        return true
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_PROXIMITY) {
            return
        }
        if (player == null || !player!!.isPlaying) {
            return
        }
        val streamType: Int = if (event.values[0] < 5f && event.values[0] != proximitySensor!!.maximumRange) {
            AudioManager.STREAM_VOICE_CALL
        } else {
            AudioManager.STREAM_MUSIC
        }
        messageAdapter.setVolumeControl(streamType)
        val position = player?.currentPosition?.toDouble() ?: 0.0
        val duration = player?.duration?.toDouble() ?: 0.0
        val progress = position / duration
        if (player!!.audioStreamType != streamType) {
            synchronized(LOCK) {
                player?.stop()
                player?.release()
                player = null
                try {
                    val currentViewHolder = currentViewHolder
                    currentViewHolder?.let { viewHolder ->
                        play(
                            viewHolder = viewHolder,
                            message = currentlyPlayingMessage,
                            earpiece = streamType == AudioManager.STREAM_VOICE_CALL,
                            progress = progress
                        )
                    }
                } catch (e: Exception) {
                    Timber.w(e)
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, i: Int) {}

    @SuppressLint("WakelockTimeout")
    private fun acquireProximityWakeLock() {
        synchronized(LOCK) {
            if (wakeLock != null) {
                wakeLock?.acquire()
            }
        }
    }

    private fun releaseProximityWakeLock() {
        synchronized(LOCK) {
            if (wakeLock != null && wakeLock?.isHeld == true) {
                wakeLock?.release()
            }
        }
        messageAdapter.setVolumeControl(AudioManager.STREAM_MUSIC)
    }

    private val currentViewHolder: ViewHolder?
        get() {
            for (audioPlayer in audioPlayerLayouts) {
                val message = audioPlayer.get()?.tag as Message
                if (message === currentlyPlayingMessage) {
                    return ViewHolder[audioPlayer.get()]
                }
            }
            return null
        }

    class ViewHolder {
        var runtime: TextView? = null
        var progress: SeekBar? = null
        var playPause: ImageButton? = null
        var darkBackground = false

        companion object {
            operator fun get(audioPlayer: RelativeLayout?): ViewHolder {
                var viewHolder = audioPlayer?.getTag(R.id.TAG_AUDIO_PLAYER_VIEW_HOLDER) as ViewHolder?
                if (viewHolder == null) {
                    viewHolder = ViewHolder()
                    viewHolder.runtime = audioPlayer?.findViewById(R.id.runtime)
                    viewHolder.progress = audioPlayer?.findViewById(R.id.progress)
                    viewHolder.playPause = audioPlayer?.findViewById(R.id.play_pause)
                    audioPlayer?.setTag(R.id.TAG_AUDIO_PLAYER_VIEW_HOLDER, viewHolder)
                }
                return viewHolder
            }
        }
    }

    companion object {
        private const val REFRESH_INTERVAL = 250
        private val LOCK = Any()
        private var player: eu.siacs.conversations.services.MediaPlayer? = null
        private var currentlyPlayingMessage: Message? = null
        private var wakeLock: WakeLock? = null
        private fun formatTime(ms: Int): String {
            return String.format(
                Locale.ENGLISH, "%d:%02d", ms / 60000, min((ms % 60000 / 1000f).roundToInt(), 59)
            )
        }
    }

    init {
        val context = adapter.context
        messageAdapter = adapter
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager?
        proximitySensor = sensorManager?.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        initializeProximityWakeLock(context)
        synchronized(LOCK) {
            if (player != null) {
                player!!.setOnCompletionListener(this)
                if (player!!.isPlaying && sensorManager != null) {
                    sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL)
                }
            }
        }
    }
}