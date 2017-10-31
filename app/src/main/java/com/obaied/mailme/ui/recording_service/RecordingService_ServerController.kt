package com.obaied.mailme.ui.recording_service

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

    fun onRecordingStarted() {
        if (EventBus.getDefault().hasSubscriberForEvent(Event_OnRecordingStarted::class.java))
            EventBus.getDefault().post(Event_OnRecordingStarted())
    }

    fun onRecordingStopped() {
        if (EventBus.getDefault().hasSubscriberForEvent(Event_OnRecordingStopped::class.java))
            EventBus.getDefault().post(Event_OnRecordingStopped())
    }

    fun onRecordingProgress(duration: String) {
        if (EventBus.getDefault().hasSubscriberForEvent(Event_OnRecordingProgress::class.java))
            EventBus.getDefault().post(Event_OnRecordingProgress(duration))
    }

    @Subscribe
    fun stopRecording(event: RecordingService_ClientController.Event_StopRecording) {
        listener.fromEventController_stopRecording()
    }

    class Event_OnRecordingStarted
    class Event_OnRecordingStopped
    class Event_OnRecordingProgress(val duration: String)

    interface ControllerListener {
        fun fromEventController_stopRecording()
    }
}