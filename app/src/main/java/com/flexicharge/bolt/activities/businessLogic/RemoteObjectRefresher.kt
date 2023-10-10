package com.flexicharge.bolt.activities.businessLogic

import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RemoteObjectRefresher<T>(
    private val remoteObject: RemoteObject<T>,
    private val refreshIntervalMilliseconds: Long
) {
    private var shouldRun: Boolean = false

    fun run(lifecycleCoroutineScope: LifecycleCoroutineScope) {
        shouldRun = true

        lifecycleCoroutineScope.launch {
            while (shouldRun) {
                val refreshJob = remoteObject.refresh(lifecycleCoroutineScope)
                refreshJob.join()
                delay(refreshIntervalMilliseconds)
            }
        }
    }

    fun stop() {
        shouldRun = false
    }
}