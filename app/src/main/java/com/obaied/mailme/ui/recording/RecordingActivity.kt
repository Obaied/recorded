package com.obaied.mailme.ui.recording

import android.os.Bundle
import com.obaied.mailme.R
import com.obaied.mailme.ui.base.BasePermissionsActivity
import kotlinx.android.synthetic.main.activity_notes.*

class RecordingActivity :
        BasePermissionsActivity(),
        RecordingFragment.RecordingFragmentListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recording)
        setSupportActionBar(toolbar)

        setupFragment()
    }

    override fun getDesiredPermissions(): Array<String> =
            arrayOf(android.Manifest.permission.RECORD_AUDIO)

    private fun setupFragment() {
        addFragment(R.id.fragment_container, RecordingFragment.makeFragment())
    }
}

