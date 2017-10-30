package com.obaied.mailme.ui.notes

import com.obaied.mailme.data.DataManager
import com.obaied.mailme.ui.base.BasePresenter
import com.obaied.mailme.util.Schedulers.SchedulerProvider
import com.obaied.mailme.util.d
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by ab on 19/03/2017.
 */

class NotesPresenter(dataManager: DataManager,
                     compositeDisposable: CompositeDisposable,
                     schedulerProvider: SchedulerProvider)
    : BasePresenter<NotesMvpView>(dataManager, compositeDisposable, schedulerProvider) {
    /**
     * Fetch all recording files from [path]
     * Process:
     * - Fetch recordings as a Single<List<File>>
     * - FlatMap to Observable<File>
     * - Map to Observable<Recording>
     * - Mark filesThatHasRecordingCounter
     * - Convert to Single<List<Recording>>
     *
     * @param path path to fetch recordings from
     *
     */
    fun fetchRecordings(path: String) {
        d { "presenter_fetchRecordings()" }

        val filesThatHasRecordingCounter = AtomicInteger()
        mCompositeDisposable.add(
                dataManager.fetchRecordingFiles(path)
                        .flatMapObservable { it -> Observable.fromIterable(it) }
                        .map { it -> dataManager.makeRecordingFromFile(it) }
                        .flatMap { it -> if (it.title.startsWith("recording")) filesThatHasRecordingCounter.incrementAndGet(); Observable.just(it) }
                        .toList()
                        .subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.ui())
                        .subscribeBy(
                                onSuccess = { listOfFiles ->
                                    d { "onSuccess: files [${listOfFiles.size}" }
                                    mvpView?.setTempRecordingPathToSharedPreferences("recording_${filesThatHasRecordingCounter.incrementAndGet()}")

                                    if (listOfFiles.isEmpty()) {
                                        mvpView?.showEmpty()
                                        return@subscribeBy
                                    }

                                    mvpView?.showRecordings(listOfFiles)
                                },
                                onError = { throwable ->
                                    d { "showErrorMessage: ${throwable.message}" }
                                    throwable.printStackTrace()
                                    mvpView?.showErrorMessage("error while fetching records")
                                })
        )
    }

    fun deleteRecording(path: String) {
        mCompositeDisposable.add(
                dataManager.deleteRecording(path)
                        .subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.ui())
                        .subscribeBy(
                                onComplete = {
                                    d { "deleteRecoding: onSuccess()" }
                                    mvpView?.onRecordingDeleted()
                                },
                                onError = { throwable ->
                                    d { "deleteRecoding: showErrorMessage()" }
                                    throwable.printStackTrace()
                                    mvpView?.showErrorMessage("error while deleting record")
                                }
                        )
        )
    }
}