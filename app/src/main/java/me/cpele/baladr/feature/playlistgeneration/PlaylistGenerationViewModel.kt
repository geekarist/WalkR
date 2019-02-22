package me.cpele.baladr.feature.playlistgeneration

import androidx.lifecycle.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext
import kotlin.math.roundToLong

class PlaylistGenerationViewModel(private val tempoDetection: TempoDetection) : ViewModel(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext = Dispatchers.Default + job

    private val _progress = MutableLiveData<Int>().apply { value = 100 }
    val progress: LiveData<Int> = _progress

    private val _tempo = Transformations.map(progress) {
        it + 70
    }
    val tempo: LiveData<Int> = _tempo

    private val tapsData = MutableLiveData<MutableList<Date>>().apply { value = mutableListOf() }

    private val _detectionRunning = MutableLiveData<Boolean>().apply { value = false }
    val tempoDetectButtonEnabled: LiveData<Boolean> = Transformations.map(_detectionRunning) { !it }
    val seekBarEnabled: LiveData<Boolean> = Transformations.map(_detectionRunning) { !it }

    val tapTempo: LiveData<Long?> = Transformations.map(tapsData) { nullableTaps: MutableList<Date>? ->
        nullableTaps?.takeIf { it.size >= 20 }?.let { taps ->
            val lowerTimeMsec = taps.minBy { it.time }?.time ?: return@map null
            val upperTimeMsec = taps.maxBy { it.time }?.time ?: return@map null
            val diffMsec = upperTimeMsec - lowerTimeMsec
            val beatsPerMsec = taps.size.toFloat() / diffMsec.toFloat()
            (beatsPerMsec * 1000 * 60).roundToLong()
        }
    }

    fun onProgressChanged(progress: Int) {
        _progress.value?.let { value ->
            if (value != progress) {
                _progress.value = (progress / 10) * 10
            }
        }
    }

    fun onStartTempoDetection(durationSeconds: Int) = launch {
        _detectionRunning.postValue(true)
        val tempo = tempoDetection.executeAsync(durationSeconds).await()
        _detectionRunning.postValue(false)
        withContext(Dispatchers.Main) { onProgressChanged(tempo - 70) }
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
}

