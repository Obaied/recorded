package com.obaied.mailme.ui.recording

import com.obaied.mailme.data.DataManager
import com.obaied.mailme.ui.base.BasePresenter
import com.obaied.mailme.util.Schedulers.SchedulerProvider
import com.obaied.mailme.util.d
import io.reactivex.disposables.CompositeDisposable
import java.io.File
import javax.inject.Inject

/**
 * Created by ab on 19/03/2017.
 */

class RecordingPresenter
@Inject constructor(dataManager: DataManager,
                    compositeDisposable: CompositeDisposable,
                    schedulerProvider: SchedulerProvider)
    : BasePresenter<RecordingMvpView>(dataManager, compositeDisposable, schedulerProvider) {

    fun renameLastRecording(currFilename: String, newNotePath: String) {
        d { "renameLastRecording()" }

        val from = File(currFilename)
        val to = File(newNotePath)

        if (!from.exists()) throw IllegalStateException("Recording file could not be found")

        from.renameTo(to)
        mvpView?.onRecordingRenamed()
    }

    fun clearTempRecording(recordingPath: String) {
        d { "clearTempRecording()" }

        val file = File(recordingPath)
        var success = false
        if (file.exists()) {
            success = file.delete()
            if (!success) mvpView?.onError("Temp file not deleted")

        } else {
            mvpView?.onError("Temp file not found")
        }

        mvpView?.tempRecordingCleared()
    }
}
