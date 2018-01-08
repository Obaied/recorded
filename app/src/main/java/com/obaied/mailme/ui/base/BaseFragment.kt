package com.joseph.mailme.ui.base

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.joseph.mailme.util.d
import dagger.android.AndroidInjection

/**
 * Created by ab on 10.10.17.
 */

open class BaseFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context?) {
        AndroidInjection.inject(this)
        super.onAttach(context)
    }

    fun toastLog(msg: String) {
        Log.d("TOAST", msg)
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
    }
}