package com.obaied.mailme

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doThrow
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.obaied.mailme.data.DataManager
import com.obaied.mailme.data.DataManagerHelper
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import java.io.File
import java.io.IOException

/**
 * Created by ab on 24.10.17.
 */

class DataManagerTest {
    lateinit var mockDataManagerHelper: DataManagerHelper
    private lateinit var dataManager: DataManager

    @Before
    fun setup() {
        mockDataManagerHelper = mock()
        dataManager = DataManager(mockDataManagerHelper)
    }

    @Test
    fun verifyTestsWork() {
        val testObserver = TestObserver<Int>()
        Observable.just(1, 2, 3).subscribe(testObserver)

        testObserver.assertNoErrors()
        testObserver.assertValues(1, 2, 3)
    }

    @Test
    fun fetchRecordings_directoryExists() {
        val listOfFiles: List<File> = listOf()

        whenever(mockDataManagerHelper
                .doesDirectoryExist_CreateIfNotExists(any()))
                .thenReturn(true)

        whenever(mockDataManagerHelper
                .listFilesInDirectory(any()))
                .thenReturn(listOfFiles)

        // TODO: changing Mockito.anyString() to any() or anyOrNull() would yield an interesting error
        // submit a PR to fix it
        val testObserver = TestObserver<List<File>>()
        dataManager.fetchRecordingFiles(Mockito.anyString())
                .subscribe(testObserver)

        testObserver.assertNoErrors()
        testObserver.assertValue(listOfFiles)
    }

    @Test
    fun fetchRecordings_DirectoryDoesNotExist() {
        whenever(mockDataManagerHelper
                .doesDirectoryExist_CreateIfNotExists(any()))
                .doThrow(IOException())

        val testObserver = TestObserver<List<File>>()
        dataManager.fetchRecordingFiles(Mockito.anyString())
                .subscribe(testObserver)

        testObserver.assertError(IOException::class.java)
    }

    @Test
    fun deleteRecording_RecordingNotFound() {
        whenever(mockDataManagerHelper
                .doesFileExist(any()))
                .thenReturn(false)

        val testObserver = TestObserver<Any>()
        dataManager.deleteRecording(Mockito.anyString())
                .subscribe(testObserver)

        testObserver.assertError(DataManager.RecordingNotFoundException::class.java)
    }

    @Test
    fun deleteRecording_RecordingNotDeleted() {
        whenever(mockDataManagerHelper
                .doesFileExist(any()))
                .thenReturn(true)

        whenever(mockDataManagerHelper
                .deleteFileOrDirectory(any()))
                .thenReturn(false)

        val testObserver = TestObserver<Any>()
        dataManager.deleteRecording(Mockito.anyString())
                .subscribe(testObserver)

        testObserver.assertError(DataManager.RecordingNotDeletedException::class.java)
    }

    @Test
    fun deleteRecording_normal() {
        whenever(mockDataManagerHelper
                .doesFileExist(any()))
                .thenReturn(true)

        whenever(mockDataManagerHelper
                .deleteFileOrDirectory(any()))
                .thenReturn(true)

        val testObserver = TestObserver<Any>()
        dataManager.deleteRecording(Mockito.anyString())
                .subscribe(testObserver)

        testObserver.assertNoErrors()
    }
}
