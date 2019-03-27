package me.cpele.baladr.feature.playlistgeneration

import android.app.Application
import android.hardware.Sensor
import android.hardware.SensorManager
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.delay
import java.util.*
import java.util.concurrent.TimeUnit

class AndroidCounterTempoDetection(private val app: Application) : TempoDetection {

    private var listener: StepCountSensorListener? = null

    private val sensorManager: SensorManager by lazy {
        app.getSystemService(Application.SENSOR_SERVICE) as SensorManager
    }

    override suspend fun executeAsync(durationSeconds: Int): Deferred<Int> {

        val deferred = CompletableDeferred<Int>().apply {
            invokeOnCompletion {
                listener?.let(sensorManager::unregisterListener)
            }
        }

        val startTimeMsec = Date().time
        val endTimeMsec = startTimeMsec + TimeUnit.SECONDS.toMillis(durationSeconds.toLong())

        sensorManager.unregisterListener(listener)
        listener = StepCountSensorListener(startTimeMsec, endTimeMsec, deferred)
        val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        sensorManager.registerListener(
            listener,
            stepSensor,
            TimeUnit.SECONDS.toMicros(3).toInt()
        )

        delay(TimeUnit.SECONDS.toMillis(durationSeconds + 3L))
        deferred.complete(0)

        return deferred
    }
}
