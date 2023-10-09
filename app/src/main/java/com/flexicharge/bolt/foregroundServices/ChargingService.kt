package com.flexicharge.bolt.foregroundServices

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.flexicharge.bolt.R
import com.flexicharge.bolt.activities.SplashscreenActivity
import com.flexicharge.bolt.adapters.TimeCalculation
import com.flexicharge.bolt.api.flexicharge.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChargingService : Service() {

    private var shouldUpdate: Boolean = true
    private var transactionId: Int = -1
    private var startTime: Long = 0
    private lateinit var notificationManager: NotificationManager
    private val updateHandler = Handler(Looper.getMainLooper())
    private val notificationBuilder = NotificationCompat.Builder(this, "charging_channel")
    private var isInitial: Boolean = true
    private var timeCalculation = TimeCalculation()

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val transactionId = sharedPreferences.getInt("TransactionId", -1)
        startTime = intent?.getLongExtra("startTime", -1)!!
        Log.d("TIME", startTime.toString())
        when (intent.action) {
            Actions.START.toString() -> start(transactionId)
            Actions.STOP.toString() -> {
                shouldUpdate = false
                stopSelf()
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun start(transaction: Int) {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        CoroutineScope(Dispatchers.IO).launch {
            transactionId = transaction
            getDataFromApi(true)
        }
        updateHandler.postDelayed(updatedNotificationTask, 3000)
    }

    private val updatedNotificationTask = object : Runnable {
        override fun run() {
            if (shouldUpdate) {
                CoroutineScope(Dispatchers.IO).launch {
                    getDataFromApi(false)
                }

                updateHandler.postDelayed(this, 3000)
            }
        }
    }

    private suspend fun getDataFromApi(firstTime: Boolean) {
        try {
            val response = RetrofitInstance.flexiChargeApi.getTransaction(transactionId)

            if (response.isSuccessful) {
                val responseData = response.body()
                val currentTime = System.currentTimeMillis()
                // val startTime = responseData?.timestamp
                val newPercentage = responseData?.currentChargePercentage.toString()
                val newTime = timeCalculation.checkDuration(startTime, currentTime)
                val updatedNotification = createNotification(newPercentage, newTime)
                if (firstTime) {
                    // StartTime = currentTime
                    startForeground(1, updatedNotification)
                } else {
                    notificationManager.notify(1, updatedNotification)
                }
            }
        } catch (e: java.lang.Exception) {
            Log.d("ChargingServiceError", "Ge transaction api error")
        }
    }

    private fun createNotification(percentage: String, timeElapsed: String): Notification {
        val contentView = RemoteViews(packageName, R.layout.layout_custom_notification)
        contentView.setTextViewText(R.id.notifcation_title, "Charging In progress")
        contentView.setTextViewText(
            R.id.notifcation_content_time_elapsed,
            "Elapsed time: $timeElapsed"
        )
        contentView.setTextViewText(
            R.id.notifcation_content_current_precantage,
            "Current percentage: $percentage"
        )

        val activityIntent = Intent(this, SplashscreenActivity::class.java)
        val id = 0
        val pendingIntent = PendingIntent.getActivity(
            this,
            id,
            activityIntent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (isInitial) {
            notificationBuilder
                .setSmallIcon(R.drawable.ic_flexicharge_banner)
                .setOnlyAlertOnce(true)

            isInitial = false
        }
        notificationBuilder
            .setOngoing(true)
            .setCustomContentView(contentView)
            .setCustomBigContentView(contentView)
            .setContentIntent(pendingIntent)

        return notificationBuilder.build()
    }

    enum class Actions {
        START, STOP
    }
}