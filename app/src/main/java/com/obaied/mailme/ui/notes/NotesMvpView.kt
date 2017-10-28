package com.obaied.mailme.ui.notes

import com.obaied.mailme.data.model.Recording
import com.obaied.mailme.ui.base.MvpView

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