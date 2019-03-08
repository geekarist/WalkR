package me.cpele.baladr.feature.playlistgeneration

import android.app.Application
import androidx.lifecycle.*
import com.github.musichin.reactivelivedata.map
import kotlinx.coroutines.*
import me.cpele.baladr.R
import kotlin.coroutines.CoroutineContext

class CalibrationViewModel(
    private val tapTempoMeasurement: TapTempoMeasurement,
    private val detection: TempoDetection,
    private val app: Application
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

    fun onTap() {
        tapTempoMeasurement.onTap()
        if (!isDetecting) {
            isDetecting = true
            launch {
                _detected.postValue(detection.executeAsync(10).await())
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
        private val app: Application
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass.cast(CalibrationViewModel(tapTempoMeasurement, detection, app)) as T
        }
    }
}
