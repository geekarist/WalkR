package me.cpele.baladr.feature.playlist_display

import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.cpele.baladr.R
import me.cpele.baladr.common.LiveEvent
import me.cpele.baladr.common.database.PlaylistBo
import me.cpele.baladr.common.database.PlaylistDao
import me.cpele.baladr.common.database.TrackBo
import me.cpele.baladr.common.database.TrackDao

class PlaylistDisplayViewModel(
    private val app: Application,
    private val trackDao: TrackDao,
    private val playlistDao: PlaylistDao
) : AndroidViewModel(app) {

    class Factory(
        private val application: Application,
        private val trackDao: TrackDao,
        private val playlistDao: PlaylistDao
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass.cast(
                PlaylistDisplayViewModel(
                    application,
                    trackDao,
                    playlistDao
                )
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

    fun onConfirmSave(playlistName: String) {
        tracksData.value?.let { tracks ->
            GlobalScope.launch {
                try {
                    val notBlankName =
                        if (playlistName.isNotBlank()) playlistName
                        else app.getString(R.string.playlist_naming_default_title)
                    val playlist = PlaylistBo(name = notBlankName)
                    val insertedPlaylistId = playlistDao.insert(playlist)
                    tracks.forEach {
                        it.playlistId = insertedPlaylistId.toInt()
                    }
                    trackDao.insertAll(tracks)
                    _playlistSaveEvent.postValue(LiveEvent(playlist))
                } catch (e: Exception) {
                    Log.d(javaClass.simpleName, "Error saving playlist", e)
                }
            }
        }
    }

    fun onConnectivityChange(status: Boolean) {
        connectivityStatusData.postValue(status)
    }
}
