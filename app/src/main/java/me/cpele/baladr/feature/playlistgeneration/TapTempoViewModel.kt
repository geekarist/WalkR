package me.cpele.baladr.feature.playlistgeneration

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class TapTempoViewModel(private val measurement: TapTempoMeasurement) : ViewModel() {
    val beatsPerMin: LiveData<Int> = measurement.beatsPerMin
    fun onTap() = measurement.onTap()
    fun onReset() = measurement.onReset()
}
