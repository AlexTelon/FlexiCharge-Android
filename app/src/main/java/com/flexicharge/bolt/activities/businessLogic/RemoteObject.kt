package com.flexicharge.bolt.activities.businessLogic

import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.Job

abstract class RemoteObject<T> {

    companion object {
        const val REMOTE_OBJECT_TIMEOUT_MILLISECONDS = 1300L
    }

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