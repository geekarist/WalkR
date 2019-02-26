package me.cpele.baladr.feature.playlistgeneration

import androidx.lifecycle.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class PlaylistGenerationViewModel(private val tempoDetection: TempoDetection) : ViewModel(), CoroutineScope {

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
                _progress.value = Math.round(progress.toFloat() / 10f) * 10
            }
        }
    }

    fun onStartTempoDetection(durationSeconds: Int) = launch {
        _detectionRunning.postValue(true)
        val tempo = tempoDetection.executeAsync(durationSeconds).await()
        _detectionRunning.postValue(false)
        withContext(Dispatchers.Main) { onProgressChanged(tempo - TEMPO_PROGRESS_OFFSET) }
    }

    fun onTempoChangedExternally(tempo: Int) {
        onProgressChanged(tempo - TEMPO_PROGRESS_OFFSET)
    }

    override fun onCleared() {
        job.cancel()
        super.onCleared()
    }

    class Factory(private val tempoDetection: TempoDetection) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass.cast(PlaylistGenerationViewModel(tempoDetection)) as T
        }
    }

    companion object {
        private const val TEMPO_PROGRESS_OFFSET = 70
        private const val DEFAULT_TEMPO = 100
    }
}

