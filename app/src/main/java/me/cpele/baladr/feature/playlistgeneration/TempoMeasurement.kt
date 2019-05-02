package me.cpele.baladr.feature.playlistgeneration

import androidx.lifecycle.LiveData

interface TempoMeasurement {
    val beatsPerMin: LiveData<Int?>
    fun onBeat()
    fun onReset()
}