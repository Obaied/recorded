package com.obaied.mailme.data.local

import android.content.Context
import android.preference.PreferenceManager
import javax.inject.Singleton

/**
 * Created by ab on 15.10.17.
 */
@Singleton
class PrefManager() {
    fun write(context: Context, key: String, value: String) {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPref.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun read(context: Context, key: String): String {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPref.getString(key, "")
    }
}