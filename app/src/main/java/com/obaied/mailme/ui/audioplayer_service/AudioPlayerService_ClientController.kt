package com.joseph.mailme.ui.audioplayer_service

import com.joseph.mailme.ui.recording_service.AudioPlayerService_ServerController
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by ab on 14.10.17.
 */
class AudioPlayerService_ClientController(private val listener: ControllerListener) {
    fun onStart() {
        EventBus.getDefault().register(this)
    }

    fun onStop() {
        EventBus.getDefault().unregister(this)
    }

    @Subscribe
    fun onPlayerInitialized(event: AudioPlayerService_ServerController.Event_onPlayerInitialized) {
        listener.fromClientController_onPlayerInitialized()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPlayerStarted(event: AudioPlayerService_ServerController.Event_OnPlayerStarted) {
        listener.fromClientController_OnPlayerStarted()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPlayerPaused(event: AudioPlayerService_ServerController.Event_OnPlayerPaused) {
        listener.fromClientController_OnPlayerPaused()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPlayerStopped(event: AudioPlayerService_ServerController.Event_OnPlayerStopped) {
        listener.fromClientController_OnPlayerStopped()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPlayerProgress(event: AudioPlayerService_ServerController.Event_OnPlayerProgress) {
        listener.fromClientController_OnPlayerProgress(event.position)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPlayerCompleted(event: AudioPlayerService_ServerController.Event_OnPlayerCompleted) {
        listener.fromClientController_OnPlayerCompleted()
    }

    //Event classes
    //TODO: This is being launched from AudioPlayerManager. Its better not to have anything outside of
    // the fragment doing the fragment work.
    class Event_TogglePlayer

    interface ControllerListener {
        fun fromClientController_onPlayerInitialized()
        fun fromClientController_OnPlayerStarted()
        fun fromClientController_OnPlayerPaused()
        fun fromClientController_OnPlayerStopped()
        fun fromClientController_OnPlayerProgress(position: Int)
        fun fromClientController_OnPlayerCompleted()
    }
}

