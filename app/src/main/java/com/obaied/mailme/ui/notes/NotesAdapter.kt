package com.obaied.mailme.ui.notes

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.obaied.mailme.R
import com.obaied.mailme.data.model.Recording
import kotlinx.android.synthetic.main.item_recording.view.*
import javax.inject.Inject

/**
 * Created by ab on 06.10.17.
 */
class NotesAdapter
@Inject constructor()
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var recordingsList: MutableList<Recording> = mutableListOf()
    var clickListener: ClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent?.context)
                .inflate(R.layout.item_recording, parent, false)

        return QuotesViewHolder(itemView)
    }

    override fun getItemCount(): Int = recordingsList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        holder as QuotesViewHolder
        val quote = recordingsList[position]
        holder.bindRecording(quote)
    }

    inner class QuotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindRecording(recording: Recording) {
            itemView.item_recording_title.text = recording.title
            itemView.item_recording_timestamp.text = recording.timestamp
            itemView.item_recording_duration.text = recording.duration
            itemView.item_recording_size.text = recording.size
            itemView.setOnClickListener { clickListener!!.onClick(recording) }
        }
    }

    interface ClickListener {
        fun onClick(recording: Recording)
    }
}

