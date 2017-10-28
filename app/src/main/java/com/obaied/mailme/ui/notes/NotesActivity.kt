package com.obaied.mailme.ui.notes

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.support.v7.view.ActionMode
import android.view.Menu
import com.obaied.mailme.R
import com.obaied.mailme.ui.audioplayer_service.AudioPlayerManager
import com.obaied.mailme.ui.audioplayer_service.AudioPlayerService_ClientController
import com.obaied.mailme.ui.base.BasePermissionsActivity
import com.obaied.mailme.ui.custom.CustomSnackBarSeekBar
import com.obaied.mailme.util.d
import kotlinx.android.synthetic.main.activity_notes.*
import javax.inject.Inject
import android.view.MenuItem

class NotesActivity :
        BasePermissionsActivity(),
        NotesFragment.NotesFragmentListener,
        AudioPlayerService_ClientController.ControllerListener {

    private var customSnackBar: CustomSnackBarSeekBar? = null
    private var actionMode: ActionMode? = null
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
        d { "onStart()" }
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
        d { "onStop()" }
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
        fab.let {
            it.setOnClickListener {
                resetActionMode()

                navigator.navigateToRecording(
                        this@NotesActivity,
                        it,
                        R.color.accent_material_dark_1,
                        android.R.drawable.ic_btn_speak_now)
            }
        }
    }

    private fun resetActionMode() {
        val fragment = getFragment() as? NotesFragment
        fragment?.fromActivity_resetSelectedRecyclerViewItem()

        actionMode?.finish()
    }

    private fun setupFragment() {
        addFragment(R.id.fragment_container, NotesFragment())
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

    override fun toggleActionMode() {
        if (actionMode != null) {
            resetActionMode()
        } else {
            startSupportActionMode(actionModeCallback)
        }
    }

    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            val menuInflater = mode.menuInflater
            menuInflater.inflate(R.menu.menu_context_note, menu)
            return true
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean =
                false // Return false if nothing is done

        // Called when the user selects a contextual menu item
        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.menu_delete -> {
                    val fragment = getFragment() as? NotesFragment
                    fragment?.fromActivity_onSelectedItemDelete()
                    mode.finish() // Action picked, so close the CAB
                    return true
                }
                else -> return false
            }
        }

        // Called when the user exits the action mode
        override fun onDestroyActionMode(mode: ActionMode) {
            actionMode = null
        }
    }
}
