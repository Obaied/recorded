package com.obaied.mailme.data

import android.os.Environment
import com.obaied.mailme.util.d
import com.obaied.mailme.util.e
import io.reactivex.Single
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by ab on 02/04/2017.
 */

@Singleton
class DataManager
@Inject constructor() {
    companion object {
        fun getVoiceNotesDir(): String = "${Environment.getExternalStorageDirectory()}/voice_notes"
    }

    fun fetchRecordings(path: String): Single<List<File>> {
        return Single.create { emitter ->
            //Fetch the recordings from the user's directory
            //Check if directory exists
            val folder = File(path)
            if (!folder.exists()) {
                d { "voice notes folder does not exist. Attempting to create" }
                val success = folder.mkdir()

                if (success) {
                    d { "voice notes folder created successfully" }
                } else {
                    e { "couldn't create voice notes folder" }
                    emitter.onError(CouldNotCreateVoiceNotesDirectoryException())
                }
                emitter.onSuccess(listOf())
            }

            //folder exists. Fetch the contents
            d { "Fetching files from $path" }
            val directory = File(path)

            val files = directory.listFiles()
            emitter.onSuccess(files.toList())
        }
    }

    inner class CouldNotCreateVoiceNotesDirectoryException : IOException()
}