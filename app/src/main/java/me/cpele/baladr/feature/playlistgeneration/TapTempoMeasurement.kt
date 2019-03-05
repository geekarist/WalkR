package me.cpele.baladr.feature.playlistgeneration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class TapTempoMeasurement {

    private val tapsData = MutableLiveData<MutableList<Long>>().apply { value = mutableListOf() }

    val beatsPerMin: LiveData<Int> = Transformations.map(tapsData) { nullableTaps: MutableList<Long>? ->
        nullableTaps
            ?.takeIf { it.size >= 2 }
            ?.let { taps ->
                val firstTapMsec = taps.min() ?: return@let null
                val lastTapMsec = taps.max() ?: return@let null
                val diffMsec = lastTapMsec - firstTapMsec
                val beatsPerMsec = taps.size.dec().toDouble() / diffMsec
                (beatsPerMsec * TimeUnit.MINUTES.toMillis(1)).roundToInt()
            } ?: 0
    }

    fun onTap() {
        val nowMsec = Date().time
        val tapsList = tapsData.value
        if (isResetDelayElapsed(tapsList)) {
            tapsList?.clear()
        }
        tapsList?.add(nowMsec)
        tapsData.value = tapsList?.toMutableList()
    }

    private fun isResetDelayElapsed(tapsList: MutableList<Long>?): Boolean {
        val nowMsec = Date().time
        val lastTapMsec = tapsList?.max() ?: return false
        return nowMsec - lastTapMsec > TimeUnit.SECONDS.toMillis(2)
    }

    fun onReset() {
        val tapsList = tapsData.value
        tapsList?.clear()
        tapsData.value = tapsList
    }
}