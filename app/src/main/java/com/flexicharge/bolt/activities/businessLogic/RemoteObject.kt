package com.flexicharge.bolt.activities.businessLogic

import androidx.lifecycle.LifecycleCoroutineScope
import com.flexicharge.bolt.api.flexicharge.Chargers
import kotlinx.coroutines.Job

abstract class RemoteObject<T> {
    data class RemoteObjectJob<T>(val job: Job, val value: T)

    abstract var value: T
        protected set

    private var onRefreshed: ((value: T) -> Unit)? = null

    protected abstract fun retrieve(lifecycleScope: LifecycleCoroutineScope) : Job

    fun setOnRefreshedCallBack(callback: (value: T) -> Unit) {
        onRefreshed = callback;
    }

    fun refresh(lifecycleScope: LifecycleCoroutineScope) : Job {

        val remoteObjectJob = retrieve(lifecycleScope)
        remoteObjectJob.invokeOnCompletion {
            if(remoteObjectJob.isCancelled) {
                return@invokeOnCompletion
            }

            onRefreshed?.invoke(value)
        }

        return remoteObjectJob
    }
}