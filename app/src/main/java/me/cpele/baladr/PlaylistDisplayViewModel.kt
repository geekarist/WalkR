package me.cpele.baladr

import android.app.Application
import android.view.View
import androidx.lifecycle.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class PlaylistDisplayViewModel(
    application: Application,
    trackDao: TrackDao,
    private val playlistDao: PlaylistDao
) : AndroidViewModel(application) {

    class Factory(
        private val application: Application,
        private val trackDao: TrackDao,
        private val playlistDao: PlaylistDao
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass.cast(PlaylistDisplayViewModel(application, trackDao, playlistDao)) as T
        }
    }

    private val tempoData: MutableLiveData<Int> = MutableLiveData()

    private val connectivityStatusData = MutableLiveData<Boolean>()

    private val _isButtonEnabled: LiveData<Boolean> = Transformations.switchMap(connectivityStatusData) { status ->
        Transformations.map(tracksData) { tracks -> status && tracks.isNotEmpty() }
    }

    val isButtonEnabled: LiveData<Boolean>
        get() = _isButtonEnabled

    val tracksData: LiveData<List<TrackBo>> = Transformations.switchMap(tempoData) { tempo ->
        trackDao.findByTempo(tempo)
    }

    val emptyViewVisibility: LiveData<Int> = Transformations.map(tracksData) {
        if (it.isEmpty()) View.VISIBLE else View.INVISIBLE
    }

    val recyclerViewVisibility: LiveData<Int> = Transformations.map(tracksData) {
        if (it.isNotEmpty()) View.VISIBLE else View.INVISIBLE
    }

    fun onPostTempo(newTempo: Int?) {
        tempoData.value = newTempo
    }

    private val _playlistSaveEvent = MutableLiveData<LiveEvent<PlaylistBo>>()
    val playlistSaveEvent: LiveData<LiveEvent<PlaylistBo>>
        get() = _playlistSaveEvent

    fun onClickSave() {
        tracksData.value?.let { tracks ->
            val trackIds = tracks.map { it.id }
            val name = Date().toString()
            val playlist = PlaylistBo(name = name, trackIds = trackIds)
            GlobalScope.launch {
                playlistDao.insert(playlist)
                _playlistSaveEvent.postValue(LiveEvent(playlist))
            }
        }
    }

    fun onConnectivityChange(status: Boolean) {
        connectivityStatusData.postValue(status)
    }
}
