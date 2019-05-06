package me.cpele.baladr.feature.playlistgeneration

import android.app.Application
import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.github.musichin.reactivelivedata.map
import kotlinx.coroutines.*
import me.cpele.baladr.R
import me.cpele.baladr.common.LiveEvent
import kotlin.coroutines.CoroutineContext

class CalibrationViewModel(
    private val tapTempoMeasurement: TapTempoMeasurement,
    private val detection: TempoDetection,
    private val app: Application,
    private val calibrationFactorRepository: CalibrationFactorRepository
) : ViewModel(), CoroutineScope {

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job

    private var isDetecting: Boolean = false

    private val _detected = MutableLiveData<Int?>().apply { value = null }
    val detectedTempoStr = _detected.map {
        it?.toString() ?: app.getString(R.string.walkr_not_applicable)
    }

    val tapTempo: LiveData<Int?> = tapTempoMeasurement.beatsPerMin.map { it }
    val tapTempoStr: LiveData<String> = Transformations.map(tapTempo) {
        it?.toString() ?: app.getString(R.string.walkr_not_applicable)
    }

    private val _viewEvent = MutableLiveData<LiveEvent<ViewEvent>>()
    val viewEvent: LiveData<LiveEvent<ViewEvent>> get() = _viewEvent

    val calibrationFactorStr: LiveData<String> = Transformations.map(calibrationFactorRepository.liveValue) {
        String.format(app.getString(R.string.calibration_factor), it)
    }

    fun onTap() = launch {
        tapTempoMeasurement.onBeat()
        if (!isDetecting) {
            isDetecting = true
            try {
                val detectedTempo = detection.execute(10)
                withContext(Dispatchers.Main) { _detected.value = detectedTempo }
                tapTempo.value?.let {
                    calibrationFactorRepository.value = it.toFloat() / detectedTempo.toFloat()
                }
                isDetecting = false
            } catch (e: Exception) {
                isDetecting = false
                withContext(Dispatchers.Main) {
                    _viewEvent.value = LiveEvent(ViewEvent.Toast(R.string.calibration_failed))
                }
                Log.w(javaClass.simpleName, "Error during tempo detection", e)
            }
        }
    }

    sealed class ViewEvent {
        data class Toast(@StringRes val message: Int) : ViewEvent()
    }

    fun onReset() {
        tapTempoMeasurement.onReset()
        job.cancelChildren()
        _detected.value = null
        isDetecting = false
    }

    override fun onCleared() {
        job.cancel()
        super.onCleared()
    }

    class Factory(
        private val tapTempoMeasurement: TapTempoMeasurement,
        private val detection: TempoDetection,
        private val app: Application,
        private val calibrationFactorRepo: CalibrationFactorRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass.cast(
                CalibrationViewModel(
                    tapTempoMeasurement,
                    detection,
                    app,
                    calibrationFactorRepo
                )
            ) as T
        }
    }
}
