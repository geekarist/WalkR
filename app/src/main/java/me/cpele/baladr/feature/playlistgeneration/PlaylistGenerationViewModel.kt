package me.cpele.baladr.feature.playlistgeneration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class PlaylistGenerationViewModel : ViewModel() {

    private val _progress = MutableLiveData<Int>().apply { value = 100 }
    val progress: LiveData<Int> = _progress

    private val _tempo = Transformations.map(progress) {
        it + 70
    }
    val tempo: LiveData<Int> = _tempo

    fun onProgressChanged(progress: Int) {
        _progress.value?.let { value ->
            if (value != progress) {
                _progress.value = (progress / 10) * 10
            }
        }
    }
}

