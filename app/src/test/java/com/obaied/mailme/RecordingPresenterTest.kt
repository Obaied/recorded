package com.joseph.mailme

import com.nhaarman.mockito_kotlin.*
import com.joseph.mailme.data.DataManager
import com.joseph.mailme.data.model.Recording
import com.joseph.mailme.ui.notes.NotesMvpView
import com.joseph.mailme.ui.notes.NotesPresenter
import com.joseph.mailme.ui.recording.RecordingMvpView
import com.joseph.mailme.ui.recording.RecordingPresenter
import com.joseph.mailme.util.Schedulers.TestSchedulerProvider
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.TestObserver
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import java.io.File
import java.io.IOException

/**
 * Created by ab on 24.10.17.
 */

class RecordingPresenterTest {
    lateinit var mockMvpView: RecordingMvpView
    lateinit var mockDataManager: DataManager
    private lateinit var presenter: RecordingPresenter

    @Before
    fun setup() {
        mockMvpView = mock()
        mockDataManager = mock()
        presenter = RecordingPresenter(
                mockDataManager,
                CompositeDisposable(),
                TestSchedulerProvider()
        )

        presenter.attachView(mockMvpView)
    }

    @After
    fun teardown() {
        presenter.detachView()
    }

    @Test
    fun verifyTestsWork() {
        val testObserver = TestObserver<Int>()
        Observable.just(1, 2, 3).subscribe(testObserver)

        testObserver.assertNoErrors()
        testObserver.assertValues(1, 2, 3)
    }

    @Test
    fun renameLastRecording_successful() {
        whenever(mockDataManager
                .renameFile(any(), any()))
                .thenReturn(Completable.complete())

        presenter.renameLastRecording(Mockito.anyString(), Mockito.anyString())

        verify(mockMvpView).onRecordingRenamed()
        verify(mockMvpView, never()).showErrorMessage(any())
    }

    @Test
    fun renameLastRecording_showError() {
        whenever(mockDataManager
                .renameFile(any(), any()))
                .thenReturn(Completable.error(IOException()))

        presenter.renameLastRecording(Mockito.anyString(), Mockito.anyString())

        verify(mockMvpView).showErrorMessage(any())
        verify(mockMvpView, never()).onRecordingRenamed()
    }

    @Test
    fun clearTempRecording_successful() {
        whenever(mockDataManager
                .deleteRecording(any()))
                .thenReturn(Completable.complete())

        presenter.clearTempRecording(Mockito.anyString())

        verify(mockMvpView).tempRecordingCleared()
        verify(mockMvpView, never()).showErrorMessage(any())
    }

    @Test
    fun clearTempRecording_showError() {
        whenever(mockDataManager
                .deleteRecording(any()))
                .thenReturn(Completable.error(IOException()))

        presenter.clearTempRecording(Mockito.anyString())

        verify(mockMvpView).showErrorMessage(any())
        verify(mockMvpView, never()).tempRecordingCleared()
    }
}