package com.joseph.mailme.ui.recording_service

import com.joseph.mailme.ui.audioplayer_service.AudioPlayerService_ClientController
import com.joseph.mailme.util.d
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * Created by ab on 14.10.17.
 */
class AudioPlayerService_ServerController(private val listener: ControllerListener) {
    fun onStart() {
        EventBus.getDefault().register(this)
    }

    fun onStop() {
        EventBus.getDefault().unregister(this)
    }

    fun onPlayerCompleted() {
        if (EventBus.getDefault().hasSubscriberForEvent(Event_OnPlayerCompleted::class.java)) {
            EventBus.getDefault().post(Event_OnPlayerCompleted())
        }
    }

    fun onPlayerInitialized() {
        EventBus.getDefault().post(Event_onPlayerInitialized())
    }

    fun onPlayerStarted() {
        if (EventBus.getDefault().hasSubscriberForEvent(Event_OnPlayerStarted::class.java)) {
            EventBus.getDefault().post(Event_OnPlayerStarted())
        }
    }

    fun onPlayerPaused() {
        if (EventBus.getDefault().hasSubscriberForEvent(Event_OnPlayerPaused::class.java)) {
            EventBus.getDefault().post(Event_OnPlayerPaused())
        }
    }

    fun onPlayerStopped() {
        if (EventBus.getDefault().hasSubscriberForEvent(Event_OnPlayerStopped::class.java)) {
            EventBus.getDefault().post(Event_OnPlayerStopped())
        }
    }

    fun onPlayerProgress(normalizedPos: Int) {
        if (EventBus.getDefault().hasSubscriberForEvent(Event_OnPlayerProgress::class.java)) {
            EventBus.getDefault().post(Event_OnPlayerProgress(normalizedPos))
        }
    }

    @Subscribe
    fun togglePlay(event: AudioPlayerService_ClientController.Event_TogglePlayer) {
        d { "service::togglePlay()" }
        listener.fromServerController_togglePlayer()
    }

    class Event_onPlayerInitialized
    class Event_OnPlayerStarted
    class Event_OnPlayerPaused
    class Event_OnPlayerStopped
    class Event_OnPlayerProgress(val position: Int)
    class Event_OnPlayerCompleted

    interface ControllerListener {
        fun fromServerController_togglePlayer()
    }
}