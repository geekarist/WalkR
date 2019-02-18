package me.cpele.baladr.feature.playlistgeneration

import android.app.Activity
import android.app.Application
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.SystemClock
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.delay
import java.util.*
import java.util.concurrent.TimeUnit

class AndroidTempoDetection(private val app: Application) : TempoDetection {

    private var listener: StepCountSensorListener? = null

    private val sensorManager: SensorManager by lazy {
        app.getSystemService(Application.SENSOR_SERVICE) as SensorManager
    }

    private inner class StepCountSensorListener(
        private val startTimeMsec: Long,
        private val endTimeMsec: Long,
        private val deferred: CompletableDeferred<Int>
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
                    val diffMsec = timestampMsec - startTimeMsec
                    val diffMin: Float = diffMsec / 1000f / 60f
                    val countPerMin = (currentCount - firstCount) / diffMin
                    deferred.complete(countPerMin.toInt() - 70)
                }
            }
        }
    }

    // No setup needed
    override fun setup(activity: Activity) = Unit

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
            SensorManager.SENSOR_DELAY_FASTEST
        )

        delay(TimeUnit.SECONDS.toMillis(11))
        deferred.complete(0)

        return deferred
    }
}
