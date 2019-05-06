package me.cpele.baladr.feature.playlistgeneration

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.*
import kotlinx.coroutines.*
import me.cpele.baladr.R
import me.cpele.baladr.common.LiveEvent
import kotlin.coroutines.CoroutineContext

class PlaylistGenerationViewModel(
    private val tempoDetection: TempoDetection,
    private val calibrationFactorRepo: CalibrationFactorRepository
) : ViewModel(), CoroutineScope {

    private val _viewEventData = MutableLiveData<LiveEvent<ViewEvent>>()
    val viewEventData: LiveData<LiveEvent<ViewEvent>> get() = _viewEventData

    private val job = Job()
    override val coroutineContext: CoroutineContext = Dispatchers.Default + job

    private val _progress = MutableLiveData<Int>().apply { value = DEFAULT_TEMPO }
    val progress: LiveData<Int> = _progress

    private val _tempo = Transformations.map(progress) {
        it + TEMPO_PROGRESS_OFFSET
    }
    val tempo: LiveData<Int> = _tempo

    private val _detectionRunning = MutableLiveData<Boolean>().apply { value = false }
    val tempoDetectButtonEnabled: LiveData<Boolean> = Transformations.map(_detectionRunning) { !it }
    val seekBarEnabled: LiveData<Boolean> = Transformations.map(_detectionRunning) { !it }

    fun onProgressChanged(progress: Int) {
        _progress.value?.let { value ->
            if (value != progress) {
                _progress.value = Math.round(progress.toFloat() / 2f) * 2
            }
        }
    }

    fun onStartTempoDetection(durationSeconds: Int) = launch {
        _detectionRunning.postValue(true)
        try {
            val tempo = tempoDetection.execute(durationSeconds)
            Log.d(javaClass.simpleName, "Detected tempo: $tempo")
            withContext(Dispatchers.Main) {
                _detectionRunning.value = false
                val calibrationFactor = calibrationFactorRepo.value
                val fixedTempo = (tempo * calibrationFactor).toInt()
                Log.d(javaClass.simpleName, "Fixed tempo: $fixedTempo")
                onProgressChanged(fixedTempo - TEMPO_PROGRESS_OFFSET)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                _detectionRunning.value = false
                _viewEventData.value = LiveEvent(ViewEvent.Toast(R.string.generation_detection_failed, e.message))
            }
            Log.w(javaClass.simpleName, "Error during tempo detection", e)
        }
    }

    fun onTempoChangedExternally(tempo: Int) {
        onProgressChanged(tempo - TEMPO_PROGRESS_OFFSET)
    }

    override fun onCleared() {
        job.cancel()
        super.onCleared()
    }

    sealed class ViewEvent {
        data class Toast(@StringRes val message: Int, val cause: String?) : ViewEvent()
    }

    class Factory(
        private val tempoDetection: TempoDetection,
        private val calibrationFactorRepo: CalibrationFactorRepository
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass.cast(PlaylistGenerationViewModel(tempoDetection, calibrationFactorRepo)) as T
        }
    }

    companion object {
        private const val TEMPO_PROGRESS_OFFSET = 70
        private const val DEFAULT_TEMPO = 100
    }
}

