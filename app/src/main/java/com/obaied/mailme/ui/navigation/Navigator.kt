package com.obaied.mailme.ui.navigation

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.view.View
import com.obaied.mailme.R
import com.obaied.mailme.ui.anims.FabTransform
import com.obaied.mailme.ui.recording.RecordingActivity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by ab on 10.10.17.
 */
@Singleton
class Navigator @Inject constructor() {
    fun navigateToRecording(activity: Activity, sharedElementView: View,
                            colorInt: Int,
                            @DrawableRes iconResId: Int) {
        val intent = Intent(activity, RecordingActivity::class.java)
        FabTransform.addExtras(
                intent,
                ContextCompat.getColor(activity, colorInt),
                iconResId
        )

        val options = ActivityOptions.makeSceneTransitionAnimation(
                activity,
                sharedElementView,
                activity.getString(R.string.transition_recording_fragment)
        )

        activity.startActivity(intent, options.toBundle())
    }
}