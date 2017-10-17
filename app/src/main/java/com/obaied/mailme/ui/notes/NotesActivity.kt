package com.obaied.mailme.ui.notes

import android.Manifest
import android.content.Context
import android.os.Bundle
import com.obaied.mailme.R
import com.obaied.mailme.ui.audioplayer_service.AudioPlayerManager
import com.obaied.mailme.ui.audioplayer_service.AudioPlayerService_ClientController
import com.obaied.mailme.ui.base.BasePermissionsActivity
import com.obaied.mailme.ui.custom.CustomSnackBarSeekBar
import com.obaied.mailme.util.d
import kotlinx.android.synthetic.main.activity_notes.*
import javax.inject.Inject

class NotesActivity :
        BasePermissionsActivity(),
        NotesFragment.NotesFragmentListener,
        AudioPlayerService_ClientController.ControllerListener {
    private var customSnackBar: CustomSnackBarSeekBar? = null
    private var audioPlayerServiceController = AudioPlayerService_ClientController(this)

    @Inject lateinit var audioPlayerManager: AudioPlayerManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)
        setSupportActionBar(toolbar)

        setupFragment()

        setupButtons()
    }

    override fun onStart() {
        super.onStart()
        audioPlayerServiceController.onStart()

        if (audioPlayerManager.isAudioServiceRunning(this)) {
            customSnackBar = CustomSnackBarSeekBar(
                    findViewById(R.id.coordinator_layout),
                    layoutInflater)
            customSnackBar?.show()
        }
    }

    override fun onDestroy() {
        resetAudioPlayer(this)

        super.onDestroy()
    }

    override fun onStop() {
        super.onStop()
        audioPlayerServiceController.onStop()

        // It would be recreated if the audio player service is running,
        //  else, it would not need to be there again, so just dismiss it for now
        dismissSnackbar()
    }

    override fun getDesiredPermissions(): Array<String> {
        return arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    override fun onVoiceNoteClicked(uri: String?) {
        d { "onVoiceNoteClicked with uri [${uri}]" }

        audioPlayerManager.togglePlayer(this, uri)
    }

    private fun setupButtons() {
        fab.setOnClickListener { navigateToRecording(this) }
    }

    private fun setupFragment() {
        addFragment(R.id.fragment_container, NotesFragment())
    }

    private fun navigateToRecording(context: Context) {
        navigator.navigateToRecording(context)
    }

    private fun resetAudioPlayer(context: Context) {
        dismissSnackbar()
        audioPlayerManager.stopPlayer(context)
    }

    private fun dismissSnackbar() {
        customSnackBar?.dismiss()
        customSnackBar = null
    }

    override fun fromClientController_onPlayerInitialized() {
        audioPlayerManager.onPlayerInitialized()

        customSnackBar = CustomSnackBarSeekBar(
                findViewById(R.id.coordinator_layout),
                layoutInflater)
        customSnackBar?.show()
    }

    override fun fromClientController_OnPlayerStarted() {
    }

    override fun fromClientController_OnPlayerPaused() {
    }

    override fun fromClientController_OnPlayerStopped() {
        d { "onPlayerStopped()" }
        resetAudioPlayer(this)
    }

    override fun fromClientController_OnPlayerProgress(position: Int) {
        d { "onPlayerProgress() with progress [${position}]" }
        customSnackBar?.setProgress(position)
    }

    override fun fromClientController_OnPlayerCompleted() {
        resetAudioPlayer(this)
    }

}
