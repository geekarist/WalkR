package me.cpele.baladr.common

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@MainThread
fun <X, Y> mapAsync(
    source: LiveData<X>,
    mapFunction: (X) -> Y?
): LiveData<Y> {
    val result = MediatorLiveData<Y>()
    result.addSource(source) { x ->
        GlobalScope.launch {
            result.postValue(mapFunction(x))
        }
    }
    return result
}

