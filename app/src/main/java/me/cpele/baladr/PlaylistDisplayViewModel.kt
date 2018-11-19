package me.cpele.baladr

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PlaylistDisplayViewModel(application: Application, trackDao: TrackDao) : AndroidViewModel(application) {

    class Factory(
        private val application: Application,
        private val trackDao: TrackDao
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass.cast(PlaylistDisplayViewModel(application, trackDao)) as T
        }
    }

    val tracks: LiveData<List<TrackBo>> = trackDao.findByTempo(80)
}
