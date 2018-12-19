package me.cpele.baladr.feature.playlistdisplay

import android.app.Application
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

    private val inputPlaylistName = MutableLiveData<String>()

    val saveMsgEvent: LiveData<LiveEvent<String>> =
        Transformations.switchMap(inputPlaylistName) { playlistName: String? ->
            val notBlankName =
                if (playlistName?.isNotBlank() == true) playlistName
                else app.getString(R.string.playlist_naming_default_title)

            Transformations.switchMap(tracksData) { tracks ->
                val playlist = PlaylistBo(0, notBlankName, tracks)

                Transformations.map(playlistRepository.insert(playlist)) {
                    LiveEvent(
                        if (it.isSuccess) {
                            app.getString(R.string.display_save_result_msg, notBlankName, tracks.size)
                        } else {
                            app.getString(R.string.display_save_error_msg, notBlankName, it.exceptionOrNull()?.message)
                        }
                    )
                }
            }
        }

    fun onConfirmSave(playlistName: String) {
        inputPlaylistName.value = playlistName
    }

    fun onConnectivityChange(status: Boolean) {
        connectivityStatusData.postValue(status)
    }
}
