package com.obaied.mailme.ui.notes

import com.obaied.mailme.data.DataManager
import com.obaied.mailme.data.model.Recording
import com.obaied.mailme.ui.base.BasePresenter
import com.obaied.mailme.util.Schedulers.SchedulerProvider
import com.obaied.mailme.util.d
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

/**
 * Created by ab on 19/03/2017.
 */

class NotesPresenter
@Inject constructor(dataManager: DataManager,
                    compositeDisposable: CompositeDisposable,
                    schedulerProvider: SchedulerProvider)
    : BasePresenter<NotesMvpView>(dataManager, compositeDisposable, schedulerProvider) {

    /**
     * Fetch all recording files from [path]
     * Process:
     * - Fetch recordings as a Single<List<File>>
     * - Convert to Observable<File>
     * - Convert to Observable<Recording>
     * - Convert to Single<List<Recording>>
     *
     * @param path path to fetch recordings from
     *
     */
    fun fetchRecordings(path: String) {
        d { "presenter_fetchRecordings()" }

        val numOfFilesThatHasRecordingCounter = AtomicInteger()
        mCompositeDisposable.add(
                mDataManager.fetchRecordings(path)
                        .flatMapObservable { it -> Observable.fromIterable(it) }
                        .map { it -> Recording.makeRecordingFromFile(it) }
                        .flatMap { it -> if (it.title.startsWith("recording")) numOfFilesThatHasRecordingCounter.incrementAndGet(); Observable.just(it) }
                        .toList()
                        .subscribeOn(mSchedulerProvider.io())
                        .observeOn(mSchedulerProvider.ui())
                        .subscribeBy(
                                onSuccess = { listOfFiles ->
                                    d { "onSuccess: files [${listOfFiles.size}" }
                                    mvpView?.setTempRecordingPathToSharedPreferences("recording_${numOfFilesThatHasRecordingCounter.incrementAndGet()}")

                                    if (listOfFiles.isEmpty()) {
                                        mvpView?.showEmpty()
                                        return@subscribeBy
                                    }

                                    mvpView?.showRecordings(listOfFiles)
                                },
                                onError = { throwable ->
                                    d { "onError: ${throwable.message}" }
                                    throwable.printStackTrace()
                                    if (throwable is DataManager.CouldNotCreateVoiceNotesDirectoryException)
                                        mvpView?.onError_CouldNotCreateVoiceNotesDirectory()
                                    else
                                        mvpView?.onError_CouldNotFetchRecordings(throwable)
                                })
        )
    }
}