package me.cpele.baladr.feature.playlistgeneration

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.*
import com.github.musichin.reactivelivedata.map
import kotlinx.coroutines.*
import me.cpele.baladr.R
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

    val calibrationFactorStr: LiveData<String> = Transformations.map(calibrationFactorRepository.liveValue) {
        String.format(app.getString(R.string.calibration_factor), it)
    }

    fun onTap() {
        tapTempoMeasurement.onBeat()
        if (!isDetecting) {
            isDetecting = true
            launch {
                val detectedTempo = detection.execute(10).await()
                _detected.postValue(detectedTempo)
                tapTempo.value?.let {
                    calibrationFactorRepository.value = it.toFloat() / detectedTempo.toFloat()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            app,
                            "Fixed tempo: ${detectedTempo * calibrationFactorRepository.value}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                isDetecting = false
            }
        }
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
