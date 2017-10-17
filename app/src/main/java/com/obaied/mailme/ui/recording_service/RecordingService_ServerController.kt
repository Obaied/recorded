package com.obaied.mailme.ui.recording_service

import com.obaied.mailme.ui.audioplayer_service.AudioPlayerService_ClientController
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * Created by ab on 14.10.17.
 */
class RecordingService_ServerController(private val listener: ControllerListener) {
    fun onStart() {
        EventBus.getDefault().register(this)
    }

    fun onStop() {
        EventBus.getDefault().unregister(this)
    }

    class Event_OnRecordingStarted

    fun onRecordingStarted() {
        EventBus.getDefault().post(Event_OnRecordingStarted())
    }

    class Event_OnRecordingStopped

    fun onRecordingStopped() {
        EventBus.getDefault().post(Event_OnRecordingStopped())
    }

    @Subscribe
    fun stopRecording(event: RecordingService_ClientController.Event_StopRecording) {
        listener.fromEventController_stopRecording()
    }

    interface ControllerListener {
        fun fromEventController_stopRecording()
    }
}