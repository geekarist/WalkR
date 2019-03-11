package me.cpele.baladr.feature.playlistgeneration

import android.content.SharedPreferences

class CalibrationFactorRepository(private val sharedPreferences: SharedPreferences) {

    var value: Float
        get() = sharedPreferences.getFloat(KEY_CALIB_FACTOR, 1f)
        set(newVal) = sharedPreferences.edit().putFloat(KEY_CALIB_FACTOR, newVal).apply()

    companion object {
        private const val KEY_CALIB_FACTOR = "KEY_CALIB_FACTOR"
    }
}
