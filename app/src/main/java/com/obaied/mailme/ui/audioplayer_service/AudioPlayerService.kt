package com.joseph.mailme.ui.audioplayer_service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import android.widget.Toast
import com.joseph.mailme.ui.notes.NotesActivity
import com.joseph.mailme.ui.recording_service.AudioPlayerService_ServerController
import com.joseph.mailme.util.d
import org.greenrobot.eventbus.EventBus
import java.util.*


/**
 * Created by ab on 11.10.17.
 */

class AudioPlayerService : Service(),
        AudioPlayerService_ServerController.ControllerListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener {
    companion object {
        val EXTRA_URI = "EXTRA_URI"
    }

    private var mediaPlayer: MediaPlayer? = null
    private var progressTimer: Timer? = null
    private val audioPlayerServerController = AudioPlayerService_ServerController(this)

    override fun onCreate() {
        super.onCreate()

        audioPlayerServerController.onStart()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val uriToPlay = intent.getStringExtra(EXTRA_URI)
        d { "onStartCommand with uri[$uriToPlay]" }

        mediaPlayer = MediaPlayer()
        val _mediaPlayer = mediaPlayer ?: throw IllegalStateException("Mediaplayer is null")

        _mediaPlayer.setDataSource(this, Uri.parse(uriToPlay))
        _mediaPlayer.setOnCompletionListener(this)
        _mediaPlayer.setOnErrorListener { _, what, extra ->
            Toast.makeText(this@AudioPlayerService,
                    "Could not play the current file; what[$what] extra [$extra]",
                    Toast.LENGTH_SHORT)
                    .show()
            return@setOnErrorListener true
        }
        _mediaPlayer.setOnPreparedListener(this)
        _mediaPlayer.prepareAsync()

        return Service.START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onDestroy() {
        d { "onDestroy()" }
        audioPlayerServerController.onStop()

        super.onDestroy()
    }

    override fun onCompletion(mp: MediaPlayer?) {
        stopPlayer()
        audioPlayerServerController.onPlayerCompleted()
    }

    override fun onPrepared(mediaPlayer: MediaPlayer) {
        audioPlayerServerController.onPlayerInitialized()

        val timerTask = object : TimerTask() {
            override fun run() {
                val normalizedPos: Int = ((mediaPlayer.currentPosition.toFloat() / mediaPlayer.duration.toFloat()) * 100).toInt()

                audioPlayerServerController.onPlayerProgress(normalizedPos)
            }
        }

        progressTimer = Timer()
        progressTimer?.schedule(timerTask, 1000, 1000)
        startPlayer(mediaPlayer)
    }

    override fun fromServerController_togglePlayer() {
        val _mediaPlayer = mediaPlayer ?: throw IllegalStateException("Mediaplayer is null")

        if (!_mediaPlayer.isPlaying) {
            startPlayer(_mediaPlayer)
        } else {
//            pausePlayer(_mediaPlayer)
            stopPlayer()
        }
    }

    private fun startPlayer(mediaPlayer: MediaPlayer) {
        mediaPlayer.start()

        audioPlayerServerController.onPlayerStarted()
    }

    private fun pausePlayer(mediaPlayer: MediaPlayer) {
        mediaPlayer.pause()

        audioPlayerServerController.onPlayerPaused()
    }

    private fun stopPlayer() {
        d { "stopPlayer()" }
        if (mediaPlayer != null) {
            mediaPlayer?.stop()
            mediaPlayer?.reset()
            mediaPlayer?.release()
            mediaPlayer = null
        }

        if (progressTimer != null) {
            progressTimer?.cancel()
            progressTimer = null
        }

        audioPlayerServerController.onPlayerStopped()

        stopSelf()
    }
}
