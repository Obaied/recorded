package com.obaied.mailme.ui.recording_service

import android.app.*
import android.content.Intent
import android.media.MediaRecorder
import android.os.Build
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import com.obaied.mailme.data.local.NotificationChannelManager
import com.obaied.mailme.ui.notes.NotesActivity
import com.obaied.mailme.ui.recording.RecordingActivity
import com.obaied.mailme.util.d
import com.obaied.mailme.util.e
import java.io.IOException

/**
 * Created by ab on 14.10.17.
 */

class RecordingService :
        Service(),
        RecordingService_ServerController.ControllerListener {
    companion object {
        val INTENT_EXTRA_RECORDING_FILENAME = "com.obaied.INTENT_EXTRA_RECORDING_FILENAME"

        val FOREGROUND_ID = 1338
        val CHANNEL_ID = 1339
    }

    private var mediaRecorder: MediaRecorder? = null
    private val recordingServiceServerController
            = RecordingService_ServerController(this)

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

        recordingServiceServerController.onRecordingStarted()
    }

    private fun stopRecording() {
        d { "stopRecording()" }

        val _mediaRecorder = mediaRecorder?.let { it } ?: throw IllegalStateException("MediaRecorder is null")

        _mediaRecorder.stop()
        _mediaRecorder.reset()
        _mediaRecorder.release()
        mediaRecorder = null

        recordingServiceServerController.onRecordingStopped()

        stopForeground(true)
        stopSelf()
    }

    private fun buildForegroundNotification(): Notification? {
        val b = NotificationCompat.Builder(this, CHANNEL_ID.toString())

        val notificationIntent = Intent(this, RecordingActivity::class.java)
        val stackBuilder = TaskStackBuilder.create(this)
        // Adds the back stack
//        stackBuilder.addParentStack(NotesActivity::class.java)

        // Add the intent to the top of the stack
        stackBuilder.addNextIntentWithParentStack(notificationIntent)

        // Make a pendingIntent containing the entire back stack
        val pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        b.setOngoing(true)
                .setContentTitle("Recording...")
                .setContentIntent(pendingIntent)
                .setSmallIcon(android.R.drawable.alert_light_frame)
                .setTicker("TICKER")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannelManager.recordingChannelId?.let { b.setChannelId(it) }
        }

        return b.build()
    }

//    private fun raiseNotification(intent: Intent?) {
//        val b = NotificationCompat.Builder(this, CHANNEL_ID.toString())
//
//        b.setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL)
//                .setWhen(System.currentTimeMillis())
//
//        b.setContentTitle("TITLE_2")
//                .setContentText("TEXT_2")
//                .setSmallIcon(android.R.drawable.stat_sys_download_done)
//                .setTicker("TICKER_2")
//
////        val outbound = Intent(Intent.ACTION_VIEW)
////        val outputUri = FileProvider.getUriForFile(this, AUTHORITY, output)
////
////        outbound.setDataAndType(outputUri, inbound.getType())
////        outbound.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
////
////        val pi = PendingIntent.getActivity(this, 0,
////                outbound, PendingIntent.FLAG_UPDATE_CURRENT)
////
////        b.setContentIntent(pi)
//
//        val mgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        mgr.notify(NOTIFY_ID, b.build())
//    }
}
