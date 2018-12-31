package me.cpele.baladr.feature.playlistgeneration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

class PlaylistGenerationViewModel : ViewModel(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext = Dispatchers.Default + job

    private val _progress = MutableLiveData<Int>().apply { value = 100 }
    val progress: LiveData<Int> = _progress

    private val _tempo = Transformations.map(progress) {
        it + 70
    }
    val tempo: LiveData<Int> = _tempo

    private val _detectionRunning = MutableLiveData<Boolean>().apply { value = false }
    val tempoDetectButtonEnabled: LiveData<Boolean> = Transformations.map(_detectionRunning) { !it }
    val seekBarEnabled: LiveData<Boolean> = Transformations.map(_detectionRunning) { !it }

    fun onProgressChanged(progress: Int) {
        _progress.value?.let { value ->
            if (value != progress) {
                _progress.value = (progress / 10) * 10
            }
        }
    }

    fun onStartTempoDetection(durationSeconds: Int) {
        launch {
            _detectionRunning.postValue(true)
            delay(TimeUnit.SECONDS.toMillis(durationSeconds.toLong()))
            _detectionRunning.postValue(false)
            val count = 15
            val tempo = count * (60 / durationSeconds)
            _progress.postValue(tempo - 70)
        }
    }

    override fun onCleared() {
        job.cancel()
        super.onCleared()
    }
}

