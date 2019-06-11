package me.cpele.baladr.feature.playlistgeneration

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import me.cpele.baladr.R

class TapTempoViewModel(
    private val measurement: TapTempoMeasurement,
    val app: Application
) : ViewModel() {

    val beatsPerMin: LiveData<Int?> = measurement.beatsPerMin
    val beatsPerMinStr: LiveData<String> = Transformations.map(beatsPerMin) {
        it?.toString() ?: app.getString(R.string.walkr_not_applicable)
    }

    fun onTap() = measurement.onBeat()
    fun onReset() = measurement.onReset()
}
