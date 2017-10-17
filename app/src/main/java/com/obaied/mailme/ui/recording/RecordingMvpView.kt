package com.obaied.mailme.ui.recording

import com.obaied.mailme.ui.base.MvpView

/**
 * Created by ab on 19/03/2017.
 */

interface RecordingMvpView : MvpView {
    fun showMessage(message: String)
    fun onError(message: String)
    fun tempRecordingCleared()
    fun onRecordingRenamed()
}
