package me.cpele.baladr.feature.playlistgeneration

import android.app.Application
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.SystemClock
import android.util.Log
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.delay
import java.util.*
import java.util.concurrent.TimeUnit

class AndroidTempoDetection(private val app: Application) : TempoDetection {

    private var listener: StepDetectSensorListener? = null

    private val sensorManager: SensorManager by lazy {
        app.getSystemService(Application.SENSOR_SERVICE) as SensorManager
    }

    private inner class StepDetectSensorListener(
        private val startTimeMsec: Long,
        private val endTimeMsec: Long,
        private val deferred: CompletableDeferred<Int>
    ) : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

        private var totalCount = 0

        override fun onSensorChanged(event: SensorEvent?) {
            event?.values?.size?.let { count ->
                totalCount += count
                val eventTimeSinceBootNsec = event.timestamp
                val eventTimeSinceBootMsec = eventTimeSinceBootNsec / 1_000_000L
                val bootTimeMsec = System.currentTimeMillis() - SystemClock.elapsedRealtime()
                val eventTimeSinceEpochMsec = bootTimeMsec + eventTimeSinceBootMsec
                Log.d(this@AndroidTempoDetection.javaClass.simpleName, "${Date(eventTimeSinceEpochMsec)}: $count")
                if (eventTimeSinceEpochMsec >= endTimeMsec) {
                    val diffMsec = eventTimeSinceEpochMsec - startTimeMsec
                    val diffMin: Float = diffMsec / 1000f / 60f
                    val countPerMin = totalCount.toFloat() / diffMin
                    Log.d(this@AndroidTempoDetection.javaClass.simpleName, "Count per min: $countPerMin")
                    deferred.complete(countPerMin.toInt())
                }
            }
        }
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
        listener = StepDetectSensorListener(startTimeMsec, endTimeMsec, deferred)
        val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        sensorManager.registerListener(
            listener,
            stepSensor,
            SensorManager.SENSOR_DELAY_FASTEST
        )

        delay(TimeUnit.SECONDS.toMillis(11))
        deferred.complete(0)

        return deferred
    }
}
