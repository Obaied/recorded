package com.obaied.mailme.ui.recording

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.EditText
import com.obaied.mailme.R

/**
 * Created by ab on 06.10.17.
 */

class SaveRecordingDialog(context: Context, private val hint: String, private val listener: onClick) :
        Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_savefile)
        setCancelable(false)
        setCanceledOnTouchOutside(false)

        val editText = findViewById<EditText>(R.id.dialog_edittext)
        editText.requestFocus()
        editText.hint = this.hint

        findViewById<View>(R.id.dialog_save).setOnClickListener {

            //If the user didn't supply a new title, just take the generated hint string
            val noteName = if (editText.text.isEmpty()) editText.hint.toString()
            else editText.text.toString()

            listener.onClick_save(noteName)
            dismiss()
        }

        findViewById<View>(R.id.dialog_discard).setOnClickListener {
            listener.onClick_discard()
            dismiss()
        }
    }

    public interface onClick {
        fun onClick_save(noteName: String)
        fun onClick_discard()
    }
}