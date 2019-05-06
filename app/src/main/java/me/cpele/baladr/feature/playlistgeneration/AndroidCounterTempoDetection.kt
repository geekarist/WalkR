package me.cpele.baladr.feature.playlistgeneration

import android.app.Application
import android.hardware.Sensor
import android.hardware.SensorManager
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

class AndroidCounterTempoDetection(private val app: Application) : TempoDetection {

    private var listener: StepCountSensorListener? = null

    private val sensorManager: SensorManager by lazy {
        app.getSystemService(Application.SENSOR_SERVICE) as SensorManager
    }

    override suspend fun execute(durationSeconds: Int): Int {
        val durationMillis = TimeUnit.SECONDS.toMillis(durationSeconds + 3L)
        return withTimeout(durationMillis) {
            suspendCancellableCoroutine { continuation: CancellableContinuation<Int> ->

                continuation.invokeOnCancellation { disposeListener() }

                val startTimeMsec = Date().time
                val endTimeMsec = startTimeMsec + TimeUnit.SECONDS.toMillis(durationSeconds.toLong())

                disposeListener()
                listener = StepCountSensorListener(startTimeMsec, endTimeMsec) {
                    disposeListener()
                    continuation.resume(it)
                }
                val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
                sensorManager.registerListener(
                    listener,
                    stepSensor,
                    TimeUnit.SECONDS.toMicros(3).toInt()
                )
            }
        }
    }

    private fun disposeListener() {
        listener?.let(sensorManager::unregisterListener)
        listener = null
    }
}

