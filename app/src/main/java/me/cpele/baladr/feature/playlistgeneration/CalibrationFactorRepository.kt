package me.cpele.baladr.feature.playlistgeneration

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class CalibrationFactorRepository(private val sharedPreferences: SharedPreferences) {

    companion object {
        private const val KEY_CALIB_FACTOR = "KEY_CALIB_FACTOR"
    }

    var value: Float
        get() = sharedPreferences.getFloat(KEY_CALIB_FACTOR, 1f)
        set(newVal) = sharedPreferences.edit().putFloat(KEY_CALIB_FACTOR, newVal).apply()

    private val _liveValue = MutableLiveData<Float>().also { it.value = value }

    val liveValue: LiveData<Float>
        get() = _liveValue

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener { prefs, key ->
            if (key == KEY_CALIB_FACTOR) {
                _liveValue.postValue(prefs.getFloat(key, 1f))
            }
        }
    }
}
