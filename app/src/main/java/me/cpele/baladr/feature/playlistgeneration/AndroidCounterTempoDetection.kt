package me.cpele.baladr.feature.playlistgeneration

import android.app.Application
import android.hardware.Sensor
import android.hardware.SensorManager
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AndroidCounterTempoDetection(private val app: Application) : TempoDetection {

    private var listener: StepCountSensorListener? = null

    private val sensorManager: SensorManager by lazy {
        app.getSystemService(Application.SENSOR_SERVICE) as SensorManager
    }

    override suspend fun execute(durationSeconds: Int): Int =
        suspendCoroutine { continuation: Continuation<Int> ->

            val startTimeMsec = Date().time
            val endTimeMsec = startTimeMsec + TimeUnit.SECONDS.toMillis(durationSeconds.toLong())

            listener?.let { sensorManager.unregisterListener(it) }
            listener = StepCountSensorListener(startTimeMsec, endTimeMsec) {
                listener?.let(sensorManager::unregisterListener)
                listener = null
                continuation.resume(it)
            }
            val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
            sensorManager.registerListener(
                listener,
                stepSensor,
                TimeUnit.SECONDS.toMicros(3).toInt()
            )
            TimeUnit.SECONDS.toMillis(durationSeconds + 3L)
            listener?.let(sensorManager::unregisterListener)
            listener = null
        }
}

