package com.joseph.mailme.ui.audioplayer_service

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.joseph.mailme.util.d
import com.joseph.mailme.util.i
import com.joseph.mailme.util.w
import org.greenrobot.eventbus.EventBus

/**
 * Created by ab on 11.10.17.
 */
class AudioPlayerManager(val activity: Activity) {
    private var didInitialize = false
    private var currentUriString: String? = null

    fun togglePlayer(context: Context, uri_string: String?) {
        d { "togglePlayer()" }

        if (currentUriString == uri_string) {
//            pausePlayer()
            stopPlayer(activity)
        } else {
            playNewRecording(context, uri_string)
        }
    }

    private fun stopPlayer(context: Context) {
        d { "stopRecording()" }

        context.stopService(Intent(context, AudioPlayerService::class.java))

        EventBus.getDefault().post(AudioPlayerService_ClientController.Event_TogglePlayer())
    }

    fun onPlayerInitialized() {
        d { "onPlayerInitialized()" }

        didInitialize = true
    }

    private fun playNewRecording(context: Context, uri_string: String?) {
        d { "playNewRecording()" }

        if (didInitialize) {
            i { "attempting to stop the player before playing a new track" }
            stopPlayer(context)
        }

        val _uri_string = uri_string ?: throw IllegalStateException("path is null | Calling onVoiceNoteClicked() with an null path")

        currentUriString = _uri_string

        val intent = Intent(context, AudioPlayerService::class.java)
        intent.putExtra(AudioPlayerService.EXTRA_URI, _uri_string)
        context.startService(intent)
    }

    private fun pausePlayer() {
        d { "pausePlayer()" }

        if (!didInitialize) {
            w { "Attempting to pause a recording before the service is initialized" }
            return
        }

        EventBus.getDefault().post(AudioPlayerService_ClientController.Event_TogglePlayer())
    }

    fun resetAudioManager() {
        didInitialize = false
        currentUriString = null
    }

    fun isAudioServiceRunning(): Boolean {
        val value = EventBus.getDefault().hasSubscriberForEvent(AudioPlayerService_ClientController.Event_TogglePlayer::class.java)
        d { "has subscriber: $value" }
        return value
    }
}
