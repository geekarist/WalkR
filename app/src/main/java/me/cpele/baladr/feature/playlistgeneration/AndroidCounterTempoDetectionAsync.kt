package me.cpele.baladr.feature.playlistgeneration

import android.app.Application
import android.hardware.Sensor
import android.hardware.SensorManager
import android.util.Log
import java.util.*
import java.util.concurrent.TimeUnit

class AndroidCounterTempoDetectionAsync(private val app: Application) {

    private val sensorManager: SensorManager by lazy {
        app.getSystemService(Application.SENSOR_SERVICE) as SensorManager
    }

    private var listener: StepCountSensorListener? = null

    fun execute(durationSeconds: Int, callback: (Int) -> Unit) {

        val startTimeMsec = Date().time
        val endTimeMsec = startTimeMsec + TimeUnit.SECONDS.toMillis(durationSeconds.toLong())

        disposeListener()
        listener = StepCountSensorListener(startTimeMsec, endTimeMsec) {
            disposeListener()
            callback(it)
        }

        val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        Log.v(javaClass.simpleName, "Registering")
        sensorManager.registerListener(
            listener,
            stepSensor,
            TimeUnit.SECONDS.toMicros(3).toInt()
        )
    }

    fun cancel() = disposeListener()

    private fun disposeListener() {
        Log.v(javaClass.simpleName, "Unregistering")
        listener?.let(sensorManager::unregisterListener)
        listener = null
    }
}
