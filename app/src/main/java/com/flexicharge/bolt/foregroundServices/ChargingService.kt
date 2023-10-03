package com.flexicharge.bolt.foregroundServices

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.text.format.DateUtils.formatElapsedTime
import android.widget.RemoteViews
import android.widget.RemoteViews.RemoteView
import androidx.core.app.NotificationCompat
import com.flexicharge.bolt.R
import com.flexicharge.bolt.api.flexicharge.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChargingService : Service() {

    private var shouldUpdate : Boolean = true
    private var elapsedTimeInSeconds = 0
    private lateinit var notificationManager: NotificationManager
    private val updateHandler = Handler(Looper.getMainLooper())
    private val notificationBuilder = NotificationCompat.Builder(this, "charging_channel")
    private var isInitial : Boolean = true

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            Actions.START.toString() -> start()
            Actions.STOP.toString() -> {
                shouldUpdate = false
                stopSelf()
            }
        }


        return super.onStartCommand(intent, flags, startId)
    }


    private fun start(){
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        CoroutineScope(Dispatchers.IO).launch {
            getDataFromApi(true)
        }
        updateHandler.postDelayed(updatedNotificationTask, 3000)


    }

    private val updatedNotificationTask = object : Runnable {
        override fun run() {
            if(shouldUpdate){
                CoroutineScope(Dispatchers.IO).launch {
                    getDataFromApi(false)
                }

                updateHandler.postDelayed(this,3000)
            }


        }
    }


    private suspend fun getDataFromApi(firstTime : Boolean){
        try {
            val response = RetrofitInstance.flexiChargeApi.getTransaction(9999)

            if (response.isSuccessful){
                val responseData = response.body()
                val newPercentage = responseData?.currentChargePercentage.toString()
                val newTime = elapsedTimeInSeconds.toString()
                val updatedNotification = createNotification(newPercentage,newTime)
                if(firstTime){
                    startForeground(1,updatedNotification)
                }else{
                    notificationManager.notify(1, updatedNotification)
                }
                elapsedTimeInSeconds += 3
            }
        } catch (e: java.lang.Exception){

        }
    }



    private fun createNotification(percentage : String, timeElapsed : String) : Notification {
        val contentView = RemoteViews(packageName,R.layout.layout_custom_notification)
        contentView.setTextViewText(R.id.notifcation_title, "Charging In progress")
        contentView.setTextViewText(R.id.notifcation_content_time_elapsed, "Elapsed time: $timeElapsed")
        contentView.setTextViewText(R.id.notifcation_content_current_precantage, "Current percentage: $percentage")
        if(isInitial){
            notificationBuilder
                .setSmallIcon(R.drawable.ic_flexicharge_banner)
                .setOnlyAlertOnce(true)
            isInitial = false
        }
        notificationBuilder
            .setOngoing(true)
            .setCustomContentView(contentView)
            .setCustomBigContentView(contentView)





        return notificationBuilder.build()
    }



    enum class Actions {
        START, STOP
    }

}