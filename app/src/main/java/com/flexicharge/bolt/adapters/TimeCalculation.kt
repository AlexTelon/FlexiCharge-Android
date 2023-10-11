package com.flexicharge.bolt.adapters

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TimeCalculation {


    fun unixToDateTime(unixTime: Long): String {
        val locale = Locale("sv", "SE")
        val sdf = SimpleDateFormat("MM/dd/yyyy HH:mm", locale)
        val netDate = Date(unixTime * 1000)

        return sdf.format(netDate)
    }

    fun checkDuration(firstTime: Long, currentTime: Long): String {
        val locale = Locale("sv", "SE")
        Log.d("EndVerify5", firstTime.toString())
        Log.d("EndVerify4", currentTime.toString())
        val durationMillis = (currentTime * 1000) - (firstTime * 1000)
        val hours = durationMillis / (60 * 60 * 1000)
        val minutes = (durationMillis % (60 * 60 * 1000)) / (60 * 1000)
        val seconds = (durationMillis % (60 * 1000)) / 1000
        return if (hours >= 1) {
            "$hours hours and $minutes minutes"
        } else if (minutes >= 1) {
            "$minutes minutes"
        } else {
            "$seconds seconds"
        }

    }
}