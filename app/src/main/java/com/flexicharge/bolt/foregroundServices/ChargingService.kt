package com.flexicharge.bolt.foregroundServices

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.text.format.DateUtils.formatElapsedTime
import androidx.core.app.NotificationCompat
import com.flexicharge.bolt.R

class ChargingService : Service() {

    private var elapsedTimeInSeconds = 0

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            Actions.START.toString() -> start()
            Actions.STOP.toString() -> stopSelf()
        }


        return super.onStartCommand(intent, flags, startId)
    }

    fun updateElapsedTime(elapsedTime : Int){

    }

    private fun start(){
        val notification = NotificationCompat.Builder(this, "charging_channel")
            .setSmallIcon(R.drawable.ic_flexicharge_banner)
            .setContentTitle("Charging In progress")
            .setContentText("Elapsed time: ${formatElapsedTime(elapsedTimeInSeconds.toLong())}")
            .build()

        startForeground(1,notification)
    }



    enum class Actions {
        START, STOP
    }

}