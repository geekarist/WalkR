package me.cpele.baladr

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlaylistGenerationViewModel : ViewModel() {

    private val _tempo = MutableLiveData<Int>().apply { value = 100 }
    val tempo: LiveData<Int> = _tempo

    fun onTempoChanged(progress: Int) {
        if (_tempo.value != progress) {
            _tempo.value = progress
        }
    }
}
