package com.joseph.mailme.data

import com.joseph.mailme.data.model.Recording
import io.reactivex.Completable
import io.reactivex.Single
import java.io.File
import java.io.IOException

/**
 * Created by ab on 02/04/2017.
 */

class DataManager(private val dataManagerHelper: DataManagerHelper) {
    fun fetchRecordingFiles(path: String): Single<List<File>> {
        return Single.create { emitter ->
            //Fetch the recordings from the user's directory
            //Check if directory exists
            try {
                if (!dataManagerHelper.doesDirectoryExist_CreateIfNotExists(path))
                    emitter.onSuccess(listOf())
            } catch (ex: IOException) {
                if (!emitter.isDisposed)
                    emitter.onError(ex)
            }

            //folder exists. Fetch the contents
            val files = dataManagerHelper.listFilesInDirectory(path)
            emitter.onSuccess(files)
        }
    }

    fun deleteRecording(path: String): Completable {
        return Completable.create { emitter ->
            if (!dataManagerHelper.doesFileExist(path)) {
                if (!emitter.isDisposed)
                    emitter.onError(RecordingNotFoundException())
            }

            if (dataManagerHelper.deleteFileOrDirectory(path)) {
                emitter.onComplete()
            } else {
                if (!emitter.isDisposed)
                    emitter.onError(RecordingNotDeletedException())
            }
        }
    }

    fun renameFile(to: String, from: String): Completable {
        return Completable.create { emitter ->
            val toFile = File(to)
            val fromFile = File(from)

            if (!fromFile.exists()) {
                if (!emitter.isDisposed)
                    emitter.onError(RecordingNotFoundException())
            }

            fromFile.renameTo(toFile)
            emitter.onComplete()
        }
    }

    fun makeRecordingFromFile(file: File): Recording
            = dataManagerHelper.makeRecordingFromFile(file)

    inner class RecordingNotFoundException : IOException()
    inner class RecordingNotDeletedException : IOException()
}