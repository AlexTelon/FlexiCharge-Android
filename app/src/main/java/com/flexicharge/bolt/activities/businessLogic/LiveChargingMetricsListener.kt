package com.flexicharge.bolt.activities.businessLogic

import android.util.Log
import com.flexicharge.bolt.api.flexicharge.WebSocketJsonMessage
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONException

class LiveChargingMetricsListener(
    userId: String,
    private val onReceivedMetrics: (metrics: WebSocketJsonMessage.LiveMetricsMessage) -> Unit
) {

    private companion object {
        const val LIVE_METRICS_URL_BASE = "ws://18.202.253.30:1337/user/"
    }

    private var webSocket: WebSocket
    private lateinit var stopCondition: (WebSocketJsonMessage.LiveMetricsMessage) -> Boolean

    init {
        val request = Request.Builder().url(LIVE_METRICS_URL_BASE + userId).build()
        val httpClient = OkHttpClient()
        val listener = object: WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                try {
                    val liveMetricsMessage = WebSocketJsonMessage.parseLiveMetrics(text)
                    onReceivedMetrics.invoke(liveMetricsMessage)
                    if(stopCondition.invoke(liveMetricsMessage)) {
                        stop()
                    }
                }
                catch (e: JSONException) {
                    Log.d("json", e.stackTraceToString())
                }
            }
        }

        webSocket = httpClient.newWebSocket(request, listener)
        httpClient.dispatcher.executorService.shutdown()
    }

    fun stopWhen(condition: (WebSocketJsonMessage.LiveMetricsMessage) -> Boolean) {
        stopCondition = condition
    }

    fun stop() {
        webSocket.cancel()
    }

}