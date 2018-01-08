package com.joseph.mailme.data

import android.media.MediaMetadataRetriever
import android.net.Uri
import com.joseph.mailme.GlobalApplication
import com.joseph.mailme.data.model.Recording
import java.io.File
import java.io.IOException
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by ab on 24.10.17.
 */
class DataManagerHelper {
    @Throws(IOException::class)
    fun doesFileExist(path: String): Boolean {
        val file = File(path)
        if (!file.isFile) {
            throw IOException("file is not a file")
        }

        return file.exists()
    }

    @Throws(IOException::class)
    fun doesDirectoryExist_CreateIfNotExists(path: String): Boolean {
        val directory = File(path)
        if (directory.exists()) {
            return true
        }

        val success = directory.mkdir()
        if (success) {
            return true
        } else {
            throw FailedToCreateDirectoryException()
        }
    }

    @Throws(IOException::class)
    fun listFilesInDirectory(path: String): List<File> {
        val directory = File(path)
        if (!directory.isDirectory) {
            throw IOException("file is not a directory")
        }

        return directory.listFiles().toList()
    }

    fun deleteFileOrDirectory(path: String?): Boolean {
        val file = File(path)
        return file.delete()
    }

    fun makeRecordingFromFile(file: File): Recording {
        fun getHumanReadableDuration(uri: Uri): String {
            val mmr = MediaMetadataRetriever()
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
        val path = file.path
        val duration = getHumanReadableDuration(Uri.parse(path))
        val fileSize = getHumanReadableFileSize(file)

        return Recording(title, timestamp, duration, fileSize, path)
    }

    inner class FailedToCreateDirectoryException : IOException()
}