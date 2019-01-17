package me.cpele.baladr.feature.playlistgeneration

import android.app.Application
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.SystemClock
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

class PlaylistGenerationViewModel(private val app: Application) : AndroidViewModel(app), CoroutineScope {

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

    private val sensorManager: SensorManager by lazy {
        app.getSystemService(Application.SENSOR_SERVICE) as SensorManager
    }

    fun onProgressChanged(progress: Int) {
        _progress.value?.let { value ->
            if (value != progress) {
                _progress.value = (progress / 10) * 10
            }
        }
    }

    private var listener: StepCountSensorListener? = null

    fun onStartTempoDetection(durationSeconds: Int) = launch {
        val startTimeMsec = Date().time
        val endTimeMsec = startTimeMsec + TimeUnit.SECONDS.toMillis(durationSeconds.toLong())
        listener?.let(sensorManager::unregisterListener)
        listener = StepCountSensorListener(startTimeMsec, endTimeMsec)
        val defaultSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        sensorManager.registerListener(
            listener,
            defaultSensor,
            SensorManager.SENSOR_DELAY_FASTEST
        )
        _detectionRunning.postValue(true)
        delay(TimeUnit.SECONDS.toMillis(11))
        _detectionRunning.postValue(false)
    }

    inner class StepCountSensorListener(
        private val startTimeMsec: Long,
        private val endTimeMsec: Long
    ) : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

        private var firstCount = 0f

        override fun onSensorChanged(event: SensorEvent?) {
            event?.values?.get(0)?.let { currentCount ->
                val timestampMsec =
                    System.currentTimeMillis() -
                            SystemClock.elapsedRealtime() +
                            TimeUnit.NANOSECONDS.toMillis(event.timestamp)
                if (firstCount == 0f) firstCount = currentCount
                if (timestampMsec >= endTimeMsec) {
                    sensorManager.unregisterListener(this)
                    Toast.makeText(
                        app,
                        "timestamp: ${Date(event.timestamp)}, endTimeMsec: ${Date(endTimeMsec)}",
                        Toast.LENGTH_LONG
                    ).show()
                    val diffMsec = timestampMsec - startTimeMsec
                    val diffMin: Float = diffMsec / 1000f / 60f
                    val countPerMin = (currentCount - firstCount) / diffMin
                    _progress.postValue(countPerMin.toInt() - 70)
                    _detectionRunning.postValue(false)
                }
            }
        }
    }

    override fun onCleared() {
        listener?.let(sensorManager::unregisterListener)
        job.cancel()
        super.onCleared()
    }
}

