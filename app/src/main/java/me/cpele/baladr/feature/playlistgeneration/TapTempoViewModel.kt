package me.cpele.baladr.feature.playlistgeneration

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TapTempoViewModel(private val measurement: TapTempoMeasurement) : ViewModel() {

    val beatsPerMin: LiveData<Int> = measurement.beatsPerMin
    fun onTap() = measurement.onTap()
    fun onReset() = measurement.onReset()

    class Factory(
        private val tapTempoMeasurement: TapTempoMeasurement
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass.cast(TapTempoViewModel(tapTempoMeasurement)) as T
        }
    }
}
