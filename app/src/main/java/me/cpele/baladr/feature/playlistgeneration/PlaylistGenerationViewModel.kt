package me.cpele.baladr.feature.playlistgeneration

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import me.cpele.baladr.R
import me.cpele.baladr.common.LiveEvent
import kotlin.coroutines.CoroutineContext

class PlaylistGenerationViewModel(
    private val tempoDetection: TempoDetection
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

    fun onStartTempoDetection(durationSeconds: Int) = launch(Dispatchers.Main) {
        _detectionRunning.value = true
        try {
            val tempo = withContext(Dispatchers.Default) {
                tempoDetection.execute(durationSeconds)
            }
            onProgressChanged(tempo - TEMPO_PROGRESS_OFFSET)
        } catch (e: TimeoutCancellationException) {
            _viewEventData.value = LiveEvent(
                ViewEvent.Toast(
                    R.string.generation_detection_failed,
                    e.message
                )
            )
        } catch (e: CancellationException) {
            // Ignore normal cancellation exception
        } catch (e: Exception) {
            _viewEventData.value = LiveEvent(
                ViewEvent.Toast(
                    R.string.generation_detection_failed,
                    e.message
                )
            )
            Log.w(javaClass.simpleName, "Error during tempo detection", e)
        } finally {
            _detectionRunning.value = false
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

    companion object {
        private const val TEMPO_PROGRESS_OFFSET = 70
        private const val DEFAULT_TEMPO = 100
    }
}

