package com.obaied.mailme.ui.notes

import android.support.v7.widget.RecyclerView
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.obaied.mailme.R
import com.obaied.mailme.data.model.Recording
import com.obaied.mailme.ui.base.BaseActivity
import kotlinx.android.synthetic.main.item_recording.view.*
import javax.inject.Inject

/**
 * Created by ab on 06.10.17.
 */
class NotesAdapter
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var recordingsList: MutableList<Recording> = mutableListOf()
    var clickListener: ClickListener? = null
    var longClickListener: LongClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent?.context)
                .inflate(R.layout.item_recording, parent, false)

        return NotesViewHolder(itemView)
    }

    override fun getItemCount(): Int = recordingsList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        holder as NotesViewHolder
        val quote = recordingsList[position]
        holder.bindRecording(quote)
    }

    inner class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindRecording(recording: Recording) {
            itemView.item_recording_title.text = recording.title.removeSuffix(BaseActivity.THREEGP)
            itemView.item_recording_timestamp.text = recording.timestamp
            itemView.item_recording_duration.text = recording.duration
            itemView.item_recording_size.text = recording.size
            itemView.setOnClickListener { clickListener!!.onClick(recording) }
            itemView.setOnLongClickListener { view: View? ->
                longClickListener!!.onLongClick(recording)
                view?.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                return@setOnLongClickListener true
            }
        }
    }

    interface ClickListener {
        fun onClick(recording: Recording)
    }

    interface LongClickListener {
        fun onLongClick(recording: Recording)
    }
}
