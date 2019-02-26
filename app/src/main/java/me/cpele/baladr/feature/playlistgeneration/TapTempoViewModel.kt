package me.cpele.baladr.feature.playlistgeneration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class TapTempoViewModel : ViewModel() {

    private val tapsData = MutableLiveData<MutableList<Date>>().apply { value = mutableListOf() }

    private val _count: LiveData<Int> = Transformations.map(tapsData) { nullableTaps: MutableList<Date>? ->
        nullableTaps
            ?.takeIf { it.size >= MIN_TAPS }
            ?.let { taps ->
                val lowerTimeMsec = taps.minBy { it.time }?.time ?: return@let null
                val upperTimeMsec = taps.maxBy { it.time }?.time ?: return@let null
                val diffMsec = upperTimeMsec - lowerTimeMsec
                val beatsPerMsec = taps.size.toFloat() / diffMsec.toFloat()
                (beatsPerMsec * TimeUnit.MINUTES.toMillis(1)).roundToInt()
            } ?: 0
    }

    val count: LiveData<Int> = _count

    fun onTap() {
        val tapsList = tapsData.value
        tapsList?.add(Date())
        tapsData.value = tapsList?.takeLast(MIN_TAPS)?.toMutableList()
    }


    fun onReset() {
        val tapsList = tapsData.value
        tapsList?.clear()
        tapsData.value = tapsList
    }

    companion object {
        private const val MIN_TAPS = 10
    }
}
