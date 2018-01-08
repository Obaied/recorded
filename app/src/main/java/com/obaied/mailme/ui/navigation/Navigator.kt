package com.joseph.mailme.ui.navigation

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.view.View
import com.joseph.mailme.R
import com.joseph.mailme.ui.about.AboutActivity
import com.joseph.mailme.ui.anims.ActivityCircularTransform
import com.joseph.mailme.ui.recording.RecordingActivity
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
        ActivityCircularTransform.addExtras(
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

    fun displayAboutScreen(context: Context) {
        val intent = Intent(context, AboutActivity::class.java)
        context.startActivity(intent)
    }
}