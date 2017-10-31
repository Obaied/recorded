package com.obaied.mailme.ui.custom

import android.annotation.SuppressLint
import android.os.Build
import android.support.design.widget.Snackbar
import android.view.LayoutInflater
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import com.obaied.mailme.R

/**
 * Created by ab on 13.10.17.
 */
class CustomSnackBarSeekBar(private val coordinatorLayoutView: View,
                            private val layoutInflater: LayoutInflater) {
    private var seekBar: SeekBar? = null
    private var snackbar: Snackbar? = null

    init {
        snackbar = makeSnackbar()
        val inflatedSnackbarCustomLayout = inflateSnackbarCustomLayout()
        seekBar = setupSeekBar(inflatedSnackbarCustomLayout)
        val snackBarLayout = snackbar?.view as Snackbar.SnackbarLayout
        snackBarLayout.addView(inflatedSnackbarCustomLayout, 0)
    }

    fun show() {
        snackbar?.show()
    }

    fun dismiss() {
        snackbar?.dismiss()
    }

    private fun makeSnackbar(): Snackbar {
        // Create the Snackbar
        val snackbar = Snackbar.make(coordinatorLayoutView, "", Snackbar.LENGTH_INDEFINITE)
        // Get the Snackbar's layout view
        val layout = snackbar.view as Snackbar.SnackbarLayout
        // Hide the text
        val textView = layout.findViewById<View>(android.support.design.R.id.snackbar_text) as TextView
        textView.visibility = View.INVISIBLE

        return snackbar
    }

    @SuppressLint("InflateParams")
    private fun inflateSnackbarCustomLayout(): View
            = layoutInflater.inflate(R.layout.layout_snackbar, null)

    @SuppressLint("ClickableViewAccessibility")
    private fun setupSeekBar(snackbarLayout: View): SeekBar? {
        val seekBar = snackbarLayout.findViewById<SeekBar>(R.id.seekbar)
        this.seekBar?.isIndeterminate = false
        this.seekBar?.max = 100
        this.seekBar?.setOnTouchListener({ _, _ -> true })

        return seekBar
    }

    fun setProgress(position: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            seekBar?.setProgress(position, true)
        } else {
            seekBar?.setProgress(position)
        }
    }
}
