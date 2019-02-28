package me.cpele.baladr.feature.playlistgeneration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*
import java.util.concurrent.TimeUnit

class TapTempoViewModel : ViewModel() {

    private val _beatsPerMin = MutableLiveData<Int>()
    val beatsPerMin: LiveData<Int> = _beatsPerMin

    private var previousTapMsec: Long = 0
    private var firstTapMsec: Long = 0
    private var tapCount: Int = 0

    fun onTap() {
        val lastTapMsec = Date().time
        val isResetDelayElapsed = lastTapMsec - previousTapMsec > TimeUnit.SECONDS.toMillis(2)
        if (isResetDelayElapsed) {
            tapCount = 0
        }

        if (tapCount == 0) {
            firstTapMsec = lastTapMsec
            tapCount = 1
        } else {
            val timeSinceFirstTapMsec = lastTapMsec - firstTapMsec
            val beatsPerMsec = tapCount.toDouble() / timeSinceFirstTapMsec
            val beatsPerMin = 60_000 * beatsPerMsec
            _beatsPerMin.value = beatsPerMin.toInt()
            tapCount++
        }

        previousTapMsec = lastTapMsec
    }


    fun onReset() {
        _beatsPerMin.value = 0
        previousTapMsec = 0
        firstTapMsec = 0
        tapCount = 0
    }
}
