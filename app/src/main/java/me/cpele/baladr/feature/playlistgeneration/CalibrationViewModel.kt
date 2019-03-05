package me.cpele.baladr.feature.playlistgeneration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CalibrationViewModel(private val tapTempoMeasurement: TapTempoMeasurement) : ViewModel() {

    class Factory(private val tapTempoMeasurement: TapTempoMeasurement) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass.cast(CalibrationViewModel(tapTempoMeasurement)) as T
        }

    }

    fun onTap() = tapTempoMeasurement.onTap()
    fun onReset() = tapTempoMeasurement.onReset()
}
