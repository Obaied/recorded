package com.obaied.mailme.ui.navigation

import android.content.Context
import android.content.Intent
import com.obaied.mailme.ui.recording.RecordingActivity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by ab on 10.10.17.
 */
@Singleton
class Navigator @Inject constructor() {
    fun navigateToRecording(context: Context) {
        context.startActivity(Intent(context, RecordingActivity::class.java))
    }
}