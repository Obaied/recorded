package com.obaied.mailme.data.model

import android.media.MediaMetadataRetriever
import android.net.Uri
import com.obaied.mailme.GlobalApplication
import com.obaied.mailme.util.d
import java.io.File
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by ab on 06.10.17.
 */

data class Recording(
        val title: String,
        val timestamp: String,
        val duration: String,
        val size: String,
        val uri_string: String) {
    companion object {
        fun makeRecordingFromFile(file: File): Recording {
            fun getHumanReadableDuration(uri: Uri): String {
                val mmr = MediaMetadataRetriever()
                d { "uri: " + uri }
                try {
                    mmr.setDataSource(GlobalApplication.appContext, uri)
                    val durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                    val millis = durationStr.toLong()
                    return String.format("%d:%d",
                            TimeUnit.MILLISECONDS.toMinutes(millis),
                            TimeUnit.MILLISECONDS.toSeconds(millis) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
                    )
                } catch (ex: IllegalArgumentException) {
                    ex.printStackTrace()
                }

                return "00:00"
            }

            fun getHumanReadableFileSize(file: File): String {
                val size = file.length()
                val df = DecimalFormat("0.00")

                val sizeKb = 1024.0f
                val sizeMb = sizeKb * sizeKb
                val sizeGb = sizeMb * sizeKb

                return when {
                    size < sizeMb -> df.format(size / sizeKb) + " KB"
                    size < sizeGb -> df.format(size / sizeMb) + " MB"
                    else -> ""
                }
            }

            val title = file.name
            val timestamp = Date(file.lastModified()).toString()
            val uri = Uri.fromFile(file)
            val duration = getHumanReadableDuration(uri)
            val fileSize = getHumanReadableFileSize(file)

            return Recording(title, timestamp, duration, fileSize, uri.toString())
        }
    }
}
