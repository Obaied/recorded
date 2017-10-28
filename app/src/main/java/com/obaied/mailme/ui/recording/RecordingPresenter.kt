package com.obaied.mailme.ui.recording

import com.obaied.mailme.data.DataManager
import com.obaied.mailme.ui.base.BasePresenter
import com.obaied.mailme.util.Schedulers.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

/**
 * Created by ab on 19/03/2017.
 */

class RecordingPresenter
@Inject constructor(dataManager: DataManager,
                    compositeDisposable: CompositeDisposable,
                    schedulerProvider: SchedulerProvider)
    : BasePresenter<RecordingMvpView>(
        dataManager,
        compositeDisposable,
        schedulerProvider) {
    fun renameLastRecording(to: String,
                            from: String) {
        mCompositeDisposable.add(
                dataManager.renameFile(to, from)
                        .subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.ui())
                        .subscribeBy(
                                onComplete = {
                                    mvpView?.onRecordingRenamed()
                                },
                                onError = { throwable ->
                                    throwable.printStackTrace()
                                    mvpView?.showErrorMessage("Recording file could not be found")
                                }
                        )
        )
    }

    fun clearTempRecording(recordingPath: String) {
        mCompositeDisposable.add(
                dataManager.deleteRecording(recordingPath)
                        .subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.ui())
                        .subscribeBy(
                                onComplete = {
                                    mvpView?.tempRecordingCleared()
                                },
                                onError = { throwable ->
                                    throwable.printStackTrace()
                                    mvpView?.showErrorMessage("error while deleting temp record")
                                }
                        )
        )
    }
}
