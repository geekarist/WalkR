package me.cpele.baladr.feature.playlistgeneration

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.os.SystemClock
import java.util.concurrent.TimeUnit

class StepCountSensorListener(
    private val startTimeMsec: Long,
    private val endTimeMsec: Long,
    private val callback: (Int) -> Unit
) : SensorEventListener {
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    private var firstCount = 0f

    override fun onSensorChanged(event: SensorEvent?) {
        event?.values?.get(0)?.let { currentCount ->
            val timestampMsec =
                System.currentTimeMillis() -
                        SystemClock.elapsedRealtime() +
                        TimeUnit.NANOSECONDS.toMillis(event.timestamp)
            if (firstCount == 0f) {
                firstCount = currentCount
            }
            if (timestampMsec >= endTimeMsec) {
                val diffMsec = timestampMsec - startTimeMsec
                val diffMin: Float = diffMsec / 1000f / 60f
                val countPerMin = (currentCount - firstCount) / diffMin
                callback(countPerMin.toInt())
            }
        }
    }
}