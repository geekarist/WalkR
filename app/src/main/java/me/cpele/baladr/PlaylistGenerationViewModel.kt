package me.cpele.baladr

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import kotlin.math.absoluteValue

class PlaylistGenerationViewModel : ViewModel() {

    private val _progress = MutableLiveData<Int>().apply { value = 100 }
    val progress: LiveData<Int> = _progress

    private val _tempo = Transformations.map(progress) {
        it + 70
    }
    val tempo: LiveData<Int> = _tempo

    fun onProgressChanged(progress: Int) {
        if (_progress.value != progress) {
            val range = 0..200 step 10
            _progress.value = range.closest(progress)
        }
    }
}

private fun IntProgression.closest(value: Int): Int? {
    var closest: Int = Int.MAX_VALUE
    this.forEach {
        val distanceToIt = (value - it).absoluteValue
        val distanceToClosest = (value - closest).absoluteValue
        if (distanceToIt < distanceToClosest) {
            closest = it
        }
    }
    return if (closest == Int.MAX_VALUE) null else closest
}
