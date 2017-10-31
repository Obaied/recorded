package com.obaied.mailme.ui.recording_service

import android.content.Context
import android.content.Intent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by ab on 14.10.17.
 */
class RecordingService_ClientController(private val listener: ControllerListener) {
    fun onStart() {
        EventBus.getDefault().register(this)
    }

    fun onStop() {
        EventBus.getDefault().unregister(this)
    }

    fun toggleRecording(context: Context, tempRecordingFilename: String) {
        if (isServiceRunning()) {
            stopRecording()
        } else {
            startRecordingService(context, tempRecordingFilename)
        }
    }

    fun isServiceRunning(): Boolean {
        return EventBus.getDefault().hasSubscriberForEvent(
                Event_StopRecording::class.java)
    }

    private fun startRecordingService(context: Context, tempRecordingFilename: String) {
        val intent = Intent(context, RecordingService::class.java)
        intent.putExtra(RecordingService.INTENT_EXTRA_RECORDING_FILENAME,
                tempRecordingFilename)
        context.startService(intent)
    }

    private fun stopRecording() {
        EventBus.getDefault().post(Event_StopRecording())
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRecordingStarted(event: RecordingService_ServerController.Event_OnRecordingStarted) {
        listener.fromClientController_OnRecordingStarted()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRecordingStopped(event: RecordingService_ServerController.Event_OnRecordingStopped) {
        listener.fromClientController_OnRecordingStopped()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRecordingProgress(event: RecordingService_ServerController.Event_OnRecordingProgress) {
        listener.fromClientController_OnRecordingProgress(event.duration)
    }

    interface ControllerListener {
        fun fromClientController_OnRecordingStarted()
        fun fromClientController_OnRecordingStopped()
        fun fromClientController_OnRecordingProgress(duration: String)
    }

    class Event_StopRecording
}

