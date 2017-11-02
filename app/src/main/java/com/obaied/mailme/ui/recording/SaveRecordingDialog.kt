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

class SaveRecordingDialog(context: Context, private val hint: String, private val listener: OnClick) :
        Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_savefile)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        window.setBackgroundDrawableResource(android.R.color.transparent) // Set to have transparent edges


        val editText = findViewById<EditText>(R.id.dialog_edittext)
        editText.requestFocus()
        editText.hint = this.hint

        findViewById<View>(R.id.dialog_save).setOnClickListener {

            //If the user didn't supply a new title, just take the generated hint string
            val noteName = if (editText.text.isEmpty()) editText.hint.toString()
            else editText.text.toString()

            listener.onClick_Save(noteName)
            dismiss()
        }

        findViewById<View>(R.id.dialog_discard).setOnClickListener {
            listener.onClick_Discard()
            dismiss()
        }
    }

    interface OnClick {
        fun onClick_Save(noteName: String)
        fun onClick_Discard()
    }
}