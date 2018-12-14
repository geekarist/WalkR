package me.cpele.baladr.feature.playlistdisplay

import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.*
import me.cpele.baladr.R
import me.cpele.baladr.common.LiveEvent
import me.cpele.baladr.common.business.PlaylistBo
import me.cpele.baladr.common.business.PlaylistRepository
import me.cpele.baladr.common.business.TrackBo
import me.cpele.baladr.common.business.TrackRepository

class PlaylistDisplayViewModel(
    private val app: Application,
    private val trackRepository: TrackRepository,
    private val playlistRepository: PlaylistRepository
) : AndroidViewModel(app) {

    class Factory(
        private val application: Application,
        private val trackRepository: TrackRepository,
        private val playlistRepository: PlaylistRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass.cast(
                PlaylistDisplayViewModel(application, trackRepository, playlistRepository)
            ) as T
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
        trackRepository.findByTempo(tempo)
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

    fun onConfirmSave(playlistName: String) {
        tracksData.value?.let { tracks ->
            try {
                val notBlankName =
                    if (playlistName.isNotBlank()) playlistName
                    else app.getString(R.string.playlist_naming_default_title)
                val playlist = PlaylistBo(0, notBlankName, tracks)
                playlistRepository.insert(playlist)
                _playlistSaveEvent.postValue(LiveEvent(playlist))
            } catch (e: Exception) {
                Log.d(javaClass.simpleName, "Error saving playlist", e)
            }
        }
    }

    fun onConnectivityChange(status: Boolean) {
        connectivityStatusData.postValue(status)
    }
}
