package com.joseph.mailme

import com.nhaarman.mockito_kotlin.*
import com.joseph.mailme.data.DataManager
import com.joseph.mailme.data.model.Recording
import com.joseph.mailme.ui.notes.NotesMvpView
import com.joseph.mailme.ui.notes.NotesPresenter
import com.joseph.mailme.util.Schedulers.TestSchedulerProvider
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.TestObserver
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import java.io.File
import java.io.IOException

/**
 * Created by ab on 24.10.17.
 */

class NotesPresenterTest {
    lateinit var mockMvpView: NotesMvpView
    lateinit var mockDataManager: DataManager
    private lateinit var presenter: NotesPresenter

    @Before
    fun setup() {
        mockMvpView = mock()
        mockDataManager = mock()
        presenter = NotesPresenter(
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

    // TODO: make into its own class
    private fun makeMockRecordingFromFile(): Recording =
            Recording("aaa", "bbb", "ccc", "ddd", "eee")

    @Test
    fun fetchRecordings_successful() {
        val listOfFiles: List<File> = listOf(
                File("aaa"),
                File("bbb"),
                File("ccc")
        )

        val listOfRecordings = mutableListOf(
                makeMockRecordingFromFile(),
                makeMockRecordingFromFile(),
                makeMockRecordingFromFile()
        )

        whenever(mockDataManager
                .fetchRecordingFiles(any()))
                .thenReturn(Single.just(listOfFiles))

        whenever(mockDataManager
                .makeRecordingFromFile(any()))
                .thenReturn(makeMockRecordingFromFile())

        presenter.fetchRecordings(Mockito.anyString())

        verify(mockMvpView).showRecordings(listOfRecordings)
        verify(mockMvpView, never()).showEmpty()
        verify(mockMvpView, never()).showErrorMessage(any())
    }

    @Test
    fun fetchRecordings_showEmpty() {
        val listOfFiles: List<File> = listOf()

        whenever(mockDataManager
                .fetchRecordingFiles(any()))
                .thenReturn(Single.just(listOfFiles))

        presenter.fetchRecordings(Mockito.anyString())

        verify(mockMvpView).showEmpty()
        verify(mockMvpView, never()).showRecordings(any())
        verify(mockMvpView, never()).showErrorMessage(any())
    }

    @Test
    fun fetchRecordings_showErrorMessage() {
        whenever(mockDataManager
                .fetchRecordingFiles(any()))
                .thenReturn(Single.error(IOException()))

        presenter.fetchRecordings(Mockito.anyString())

        verify(mockMvpView).showErrorMessage(any())
        verify(mockMvpView, never()).showEmpty()
        verify(mockMvpView, never()).showRecordings(any())
    }

    @Test
    fun deleteRecording_successful() {
        whenever(mockDataManager
                .deleteRecording(any()))
                .thenReturn(Completable.complete())

        presenter.deleteRecording(Mockito.anyString())

        verify(mockMvpView).onRecordingDeleted()
        verify(mockMvpView, never()).showErrorMessage(any())
    }

    @Test
    fun deleteRecording_showErrorMessage() {
        whenever(mockDataManager
                .deleteRecording(any()))
                .thenReturn(Completable.error(IOException()))

        presenter.deleteRecording(Mockito.anyString())

        verify(mockMvpView).showErrorMessage(any())
        verify(mockMvpView, never()).onRecordingDeleted()
    }
}
