package com.obaied.mailme.data.local

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.obaied.mailme.R

/**
 * Created by ab on 17.10.17.
 */
object NotificationChannelManager {
    var recordingChannelId: String? = null

    @TargetApi(Build.VERSION_CODES.O)
    fun makeNotificationChannels(context: Context) {
        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // The id of the channel.
        val id = context.getString(R.string.channel_id)
        // The user-visible name of the channel.
        val name = context.getString(R.string.channel_name)
        // The user-visible description of the channel.
        val description = context.getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_LOW
        val mChannel = NotificationChannel(id, name, importance)
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