package com.joseph.mailme.ui.notes

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.joseph.mailme.R
import com.joseph.mailme.data.local.PrefManager
import com.joseph.mailme.data.model.Recording
import com.joseph.mailme.ui.base.BaseActivity
import com.joseph.mailme.ui.base.BasePermissionsFragment
import com.joseph.mailme.util.d
import kotlinx.android.synthetic.main.fragment_notes.*
import javax.inject.Inject

/**
 * Created by ab on 10.10.17.
 */
class NotesFragment :
        BasePermissionsFragment(),
        NotesMvpView {
    init {
        retainInstance = true
    }

    private var fragmentListener: NotesFragmentListener? = null
    private var currentSelectedItem: Recording? = null

    @Inject lateinit var presenter: NotesPresenter
    @Inject lateinit var notesAdapter: NotesAdapter
    @Inject lateinit var prefManager: PrefManager

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (activity is NotesFragmentListener) {
            fragmentListener = activity as NotesFragmentListener
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.fragment_notes, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        presenter.attachView(this)
    }

    override fun onResume() {
        super.onResume()

        //Called in onResume so it would be called when re-entering as well
        fetchRecordings()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun onDetach() {
        super.onDetach()
        fragmentListener = null
    }

    override fun showRecordings(recordings: MutableList<Recording>) {
        notesAdapter.recordingsList.clear()
        notesAdapter.recordingsList.addAll(recordings)
        notesAdapter.notifyDataSetChanged()
    }

    override fun showErrorMessage(message: String) {
        toastLog(message)
    }

    override fun showEmpty() {
//        toastLog(
//                "no recordings to show"
//        )
    }

    override fun setTempRecordingPathToSharedPreferences(tempRecordingPath: String) {
        val path = "${this.activity.externalCacheDir}/$tempRecordingPath.3gp"
        prefManager.write(activity, getString(R.string.preference_temp_recording_path), path)
    }

    override fun onRecordingDeleted() {
        d { "onRecordingDeleted()" }
        notesAdapter.recordingsList.remove(currentSelectedItem)
        notesAdapter.notifyDataSetChanged()
        currentSelectedItem = null
    }

    private fun setupRecyclerView() {
        //Clicklistener for recordings adapter
        notesAdapter.clickListener = object : NotesAdapter.ClickListener {
            override fun onClick(recording: Recording) {
                fragmentListener?.onVoiceNoteClicked(recording.path)
            }
        }

        notesAdapter.longClickListener = object : NotesAdapter.LongClickListener {
            override fun onLongClick(recording: Recording) {
                currentSelectedItem = recording
                fragmentListener?.toggleActionMode()
            }
        }

        //Recyclerview
        val layoutManager = LinearLayoutManager(activity)
        notes_recyclerview.layoutManager = layoutManager
        notes_recyclerview.setHasFixedSize(true)
        notes_recyclerview.adapter = notesAdapter
        notes_recyclerview.addItemDecoration(DividerItemDecoration(notes_recyclerview.context,
                layoutManager.orientation))
    }

    private fun fetchRecordings() {
        d { "fetchRecordings()" }

        if (!didGrantPermissions()) {
            return
        }

        presenter.fetchRecordings(BaseActivity.getVoiceNotesDir())
    }

    internal fun fromActivity_onSelectedItemDelete() {
        currentSelectedItem?.let {
            presenter.deleteRecording(it.path)
        }
    }

    internal fun fromActivity_resetSelectedRecyclerViewItem() {
        currentSelectedItem = null
    }

    interface NotesFragmentListener {
        fun onVoiceNoteClicked(uri: String?)
        fun toggleActionMode()
    }
}