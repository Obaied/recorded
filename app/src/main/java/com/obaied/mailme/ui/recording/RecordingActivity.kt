package com.obaied.mailme.ui.recording

import android.content.Intent
import android.os.Bundle
import com.obaied.mailme.R
import com.obaied.mailme.ui.base.BasePermissionsActivity
import com.obaied.mailme.ui.recording_service.RecordingService
import com.obaied.mailme.util.d

class RecordingActivity :
        BasePermissionsActivity(),
        RecordingFragment.RecordingFragmentListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recording)

        setupFragment()
    }

    override fun onNewIntent(intent: Intent) {
        d { "onNewIntent()" }
        super.onNewIntent(intent)

        // Check if this intent came from a notification
        if (!intent.hasExtra(RecordingService.NOTIFICATION_INTENT_EXTRA)) {
            return
        }

        // Send a signal to the fragment to reset UI as if the recording is active
        val recordingFragment = getFragment() as RecordingFragment
        recordingFragment.resetUi(true)
    }

    override fun getDesiredPermissions(): Array<String> =
            arrayOf(android.Manifest.permission.RECORD_AUDIO)

    override fun onBackPressed() {
        finishAfterTransition()
    }

    private fun setupFragment() {
        addFragment(R.id.fragment_container, RecordingFragment())
    }
}