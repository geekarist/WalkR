package me.cpele.baladr

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class PlaylistGenerationViewModel : ViewModel() {

    private val _progress = MutableLiveData<Int>().apply { value = 100 }
    val progress: LiveData<Int> = _progress

    private val _tempo = Transformations.map(progress) {
        it + 60
    }
    val tempo: LiveData<Int> = _tempo

    fun onProgressChanged(progress: Int) {
        if (_progress.value != progress) {
            _progress.value = progress
        }
    }
}
