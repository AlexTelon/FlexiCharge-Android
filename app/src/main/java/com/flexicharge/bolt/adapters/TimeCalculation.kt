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

    fun checkDuration(firstTime: Long, latestTime: Long): String {
        val durationMillis = (latestTime) - (firstTime)
        Log.d("EndVerify668", latestTime.toString())
        Log.d("EndVerify669", firstTime.toString())
        Log.d("EndVerify670", durationMillis.toString())
        val hours = durationMillis / (60 * 60 * 1000)
        val minutes = (durationMillis % (60 * 60 * 1000)) / (60 * 1000)
        val seconds = (durationMillis % (60 * 1000)) / 1000

        val returnString = if (hours >= 1) {
            if (minutes >= 1) {
                "$hours hours and $minutes minutes"
            } else {
                "$hours hours"
            }
        } else if (minutes >= 1) {
            if (seconds >= 1) {
                "$minutes minutes and $seconds seconds"
            } else {
                "$minutes minutes"
            }
        } else {
            "$seconds seconds"
        }
        return returnString
    }
}