package com.obaied.mailme.ui.notes

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.obaied.mailme.R
import com.obaied.mailme.data.DataManager
import com.obaied.mailme.data.local.PrefManager
import com.obaied.mailme.data.model.Recording
import com.obaied.mailme.ui.base.BasePermissionsFragment
import com.obaied.mailme.ui.recording.RecordingFragment
import com.obaied.mailme.util.d
import kotlinx.android.synthetic.main.fragment_notes.*
import javax.inject.Inject

/**
 * Created by ab on 10.10.17.
 */
class NotesFragment : BasePermissionsFragment(), NotesMvpView {
    companion object {
        val PERMISSIONREQUEST_EXTERNAL_STORAGE = 666
    }

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

    override fun onError_CouldNotCreateVoiceNotesDirectory() {
        toastLog(
                "couldn't make voice_notes directory"
        )
    }

    override fun onError_CouldNotFetchRecordings(throwable: Throwable) {
        toastLog(
                "could not fetch recordings"
        )
    }

    override fun showEmpty() {
        toastLog(
                "no recordings to show"
        )
    }

    override fun setTempRecordingPathToSharedPreferences(tempRecordingPath: String) {
        val path = "${this.activity.externalCacheDir}/$tempRecordingPath.3gp"
        prefManager.write(activity, getString(R.string.preference_temp_recording_path), path)
    }

    private fun setupRecyclerView() {
        //Clicklistener for recordings adapter
        notesAdapter.clickListener = object : NotesAdapter.ClickListener {
            override fun onClick(recording: Recording) {
                fragmentListener?.onVoiceNoteClicked(recording.uri_string)
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

        presenter.fetchRecordings(DataManager.getVoiceNotesDir())
    }

    internal fun fromActivity_onSelectedItemDelete() {
        //TODO: presenter.deleteFile
        Toast.makeText(activity, "deleting...${currentSelectedItem?.title}", Toast.LENGTH_SHORT).show()
    }

    internal fun fromActivity_resetSelectedRecyclerViewItem() {
        currentSelectedItem = null
    }

    interface NotesFragmentListener {
        fun onVoiceNoteClicked(uri: String?)
        fun toggleActionMode()
    }
}