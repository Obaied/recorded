package com.joseph.mailme.ui.recording_service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.app.TaskStackBuilder
import android.content.Intent
import android.media.MediaRecorder
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.support.v4.app.NotificationCompat
import com.joseph.mailme.R
import com.joseph.mailme.data.local.NotificationChannelManager
import com.joseph.mailme.ui.recording.RecordingActivity
import com.joseph.mailme.util.AppUtil
import com.joseph.mailme.util.AppUtil.getHumanReadableDuration
import com.joseph.mailme.util.d
import com.joseph.mailme.util.e
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by ab on 14.10.17.
 */

class RecordingService :
        Service(),
        RecordingService_ServerController.ControllerListener {
    companion object {
        val INTENT_EXTRA_RECORDING_FILENAME = "com.joseph.INTENT_EXTRA_RECORDING_FILENAME"
        val NOTIFICATION_INTENT_EXTRA = "com.joseph.NOTIFICATION_INTENT_EXTRA"

        val FOREGROUND_ID = 1338
        val CHANNEL_ID = 1339
    }

    private var mediaRecorder: MediaRecorder? = null
    private val recordingServiceServerController
            = RecordingService_ServerController(this)
    private var progressTimer: Timer? = null

    override fun onCreate() {
        super.onCreate()

        recordingServiceServerController.onStart()

        startForeground(FOREGROUND_ID,
                buildForegroundNotification())
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        check(intent.hasExtra(INTENT_EXTRA_RECORDING_FILENAME), { "service created with missing extra: INTENT_EXTRA_TEMP_RECORDING_FILENAME" })
        val tempRecordingFilename: String = intent.getStringExtra(INTENT_EXTRA_RECORDING_FILENAME)
        startRecording(tempRecordingFilename)

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        throw IllegalStateException("RecordingService.onBind() got called")
    }

    override fun onDestroy() {
        recordingServiceServerController.onStop()

        super.onDestroy()
    }

    override fun fromEventController_stopRecording() {
        d { "fromEventController_stopRecording" }

        stopRecording()
    }

    private fun startRecording(recordingFilename: String) {
        d { "startRecording()" }

        mediaRecorder = MediaRecorder()
        val _mediaRecorder = mediaRecorder?.let { it } ?: return
        _mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        _mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        _mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        _mediaRecorder.setOutputFile(recordingFilename)

        try {
            _mediaRecorder.prepare()
        } catch (ex: IOException) {
            e { "mediaRecorder.prepare() failed: " + ex.localizedMessage }
        }

        _mediaRecorder.start()

        // init timer
        progressTimer = Timer()
        progressTimer?.schedule(DurationTimerTask(), 0, 1000)

        recordingServiceServerController.onRecordingStarted()
    }

    private fun stopRecording() {
        d { "stopRecording()" }

        if (mediaRecorder != null) {
            mediaRecorder?.stop()
            mediaRecorder?.reset()
            mediaRecorder?.release()
            mediaRecorder = null
        }

        if (progressTimer != null) {
            progressTimer?.cancel()
            progressTimer = null
        }

        recordingServiceServerController.onRecordingStopped()

        stopForeground(true)
        stopSelf()
    }

    private fun buildForegroundNotification(): Notification? {
        val b = NotificationCompat.Builder(this, CHANNEL_ID.toString())

        val notificationIntent = Intent(this, RecordingActivity::class.java)
        notificationIntent.putExtra(NOTIFICATION_INTENT_EXTRA, true)

        // Adds the back stack
        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addNextIntentWithParentStack(notificationIntent)

        // Make a pendingIntent containing the entire back stack
        val pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        b.setOngoing(true)
                .setContentTitle("Recording")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_recording_notification)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannelManager.recordingChannelId?.let { b.setChannelId(it) }
        }

        return b.build()
    }

    inner class DurationTimerTask : TimerTask() {
        private val startTime: Long = SystemClock.uptimeMillis()

        override fun run() {
            val delta = SystemClock.uptimeMillis() - startTime
            recordingServiceServerController.onRecordingProgress(AppUtil.getHumanReadableDuration(delta))
        }
    }
}
