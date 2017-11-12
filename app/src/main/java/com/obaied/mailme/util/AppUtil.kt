package com.obaied.mailme.util

import java.util.concurrent.TimeUnit

/**
 * Created by ab on 11.11.17.
 */
object AppUtil {
    fun getHumanReadableDuration(millis: Long): String {
        val _millis = millis
        val hours = TimeUnit.MILLISECONDS.toHours(_millis)

        val minutes = TimeUnit.MILLISECONDS.toMinutes(_millis)
        - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(_millis))

        val seconds = TimeUnit.MILLISECONDS.toSeconds(_millis)
        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(_millis))

//                val minutes = TimeUnit.MILLISECONDS.toMinutes(_millis)
//                _millis -= TimeUnit.MINUTES.toMillis(_millis)
//
//                val seconds = TimeUnit.MILLISECONDS.toSeconds(_millis)

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}