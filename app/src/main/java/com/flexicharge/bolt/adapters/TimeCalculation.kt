package com.flexicharge.bolt.adapters

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TimeCalculation {
    private var _duration: String = ""
    var duration get() = _duration



    init{
        duration = _duration
    }


     fun unixToDateTime(unixTime: String) : String {
        val locale = Locale("sv","SE")
        val sdf = SimpleDateFormat("MM/dd/HH:mm", locale)
        val netDate = Date(unixTime.toLong())
        return sdf.format(netDate)
    }

    fun checkDuration(firstTime: Long, currentTime: Long) {
        val locale = Locale("sv", "SE")
        val sdf = SimpleDateFormat("HH 'hours' mm 'minutes'", locale)
        val durationMillis = currentTime - firstTime
        val hours = durationMillis / (60 * 60 * 1000)
        val minutes = (durationMillis % (60 * 60 * 1000)) / (60 * 1000)
        val seconds = (durationMillis % (60 * 1000)) / 1000
        _duration = if(hours >= 1){
            "$hours hours and $minutes minutes"
        }else if (minutes >= 1){
            "$minutes minutes"
        }else{
            "$seconds seconds"
        }

    }
}