package com.obaied.mailme.data.local

import android.content.Context
import android.os.Build
import com.obaied.mailme.R

/**
 * Created by ab on 17.10.17.
 */
object NotificationChannelManager {
    var recordingChannelId: String? = null

    fun makeNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        // The id of the channel.
        val id = context.getString(R.string.channel_id)
        // The user-visible name of the channel.
        val name = context.getString(R.string.channel_name)
        // The user-visible description of the channel.
        val description = context.getString(R.string.channel_description)
        val importance = android.app.NotificationManager.IMPORTANCE_LOW
        val mChannel = android.app.NotificationChannel(id, name, importance)
        // Configure the notification channel.
        mChannel.description = description
        mChannel.enableLights(false)
        // Sets the notification light color for notifications posted to this
        // channel, if the device supports this feature.
        mChannel.enableVibration(false)
        mNotificationManager.createNotificationChannel(mChannel)

        recordingChannelId = id
    }
}