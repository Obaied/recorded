package com.obaied.mailme.ui.recording

import android.os.Bundle
import com.obaied.mailme.R
import com.obaied.mailme.ui.base.BasePermissionsActivity
import kotlinx.android.synthetic.main.activity_recording_dialog.*

class RecordingActivity :
        BasePermissionsActivity(),
        RecordingFragment.RecordingFragmentListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recording_dialog)

        setupFragment()

        setupButtons()
    }

    override fun getDesiredPermissions(): Array<String> =
            arrayOf(android.Manifest.permission.RECORD_AUDIO)

    override fun onBackPressed() {
        finishAfterTransition()
    }

    private fun setupFragment() {
        addFragment(R.id.fragment_container, RecordingFragment())
    }

    private fun setupButtons() {
        root_layout.let {
            root_layout.setOnClickListener {
                this@RecordingActivity.finishAfterTransition()
            }
        }
    }
}

