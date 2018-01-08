package com.joseph.mailme.ui.recording

import com.joseph.mailme.ui.base.MvpView

/**
 * Created by ab on 19/03/2017.
 */

interface RecordingMvpView : MvpView {
    fun showErrorMessage(message: String)
    fun tempRecordingCleared()
    fun onRecordingRenamed()
}
