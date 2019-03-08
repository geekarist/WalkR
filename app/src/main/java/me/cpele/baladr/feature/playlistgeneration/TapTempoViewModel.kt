package me.cpele.baladr.feature.playlistgeneration

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.cpele.baladr.R

class TapTempoViewModel(
    private val measurement: TapTempoMeasurement,
    val app: Application
) : ViewModel() {

    val beatsPerMin: LiveData<Int?> = measurement.beatsPerMin
    val beatsPerMinStr: LiveData<String> = Transformations.map(beatsPerMin) {
        it?.toString() ?: app.getString(R.string.walkr_not_applicable)
    }

    fun onTap() = measurement.onTap()
    fun onReset() = measurement.onReset()

    class Factory(
        private val tapTempoMeasurement: TapTempoMeasurement,
        val app: Application
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass.cast(TapTempoViewModel(tapTempoMeasurement, app)) as T
        }
    }
}
