package com.joseph.mailme.ui.notes

import com.joseph.mailme.data.model.Recording
import com.joseph.mailme.ui.base.MvpView

/**
 * Created by ab on 19/03/2017.
 */

interface NotesMvpView : MvpView {
    fun showRecordings(recordings: MutableList<Recording>)
    fun showErrorMessage(message: String)
    fun showEmpty()
    fun setTempRecordingPathToSharedPreferences(tempRecordingPath: String)
    fun onRecordingDeleted()
}