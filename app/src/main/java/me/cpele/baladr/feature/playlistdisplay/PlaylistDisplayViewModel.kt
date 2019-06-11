package me.cpele.baladr.feature.playlistdisplay

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.musichin.reactivelivedata.combineLatestWith
import com.github.musichin.reactivelivedata.map
import com.github.musichin.reactivelivedata.switchMap
import me.cpele.baladr.R
import me.cpele.baladr.common.LiveEvent
import me.cpele.baladr.common.business.PlaylistBo
import me.cpele.baladr.common.business.PlaylistRepository
import me.cpele.baladr.common.business.TrackBo
import me.cpele.baladr.common.business.TrackRepository
import java.util.*

class PlaylistDisplayViewModel(
    private val app: Application,
    private val trackRepository: TrackRepository,
    private val playlistRepository: PlaylistRepository
) : AndroidViewModel(app) {

    private val tempoData: MutableLiveData<Int> = MutableLiveData()

    private val connectivityStatusData = MutableLiveData<Boolean>()

    private val _isButtonEnabled: LiveData<Boolean> = connectivityStatusData.switchMap { status ->
        tracksData.map { tracks -> status && tracks.isNotEmpty() }
    }

    val isButtonEnabled: LiveData<Boolean>
        get() = _isButtonEnabled

    val tracksData: LiveData<List<TrackBo>> = tempoData.switchMap { tempo ->
        trackRepository.findByTempo(tempo)
    }

    val emptyViewVisibility: LiveData<Int> = tracksData.map {
        if (it.isEmpty()) View.VISIBLE else View.INVISIBLE
    }

    val recyclerViewVisibility: LiveData<Int> = tracksData.map {
        if (it.isNotEmpty()) View.VISIBLE else View.INVISIBLE
    }

    fun onPostTempo(newTempo: Int?) {
        tempoData.value = newTempo
    }

    private val inputPlaylistName = MutableLiveData<String>()

    val saveMsgEvent: LiveData<LiveEvent<String>> =
        inputPlaylistName.map { playlistName: String? ->
            if (playlistName?.isNotBlank() == true) playlistName
            else app.getString(R.string.playlist_naming_default_title)
        }.combineLatestWith(tracksData) { notBlankName, tracks ->
            Pair(notBlankName, tracks)
        }.switchMap {
            val (notBlankName, tracks) = it
            playlistRepository.insert(PlaylistBo(0, notBlankName, tracks, date = Date()))
        }.map {
            if (it.isSuccess) {
                app.getString(
                    R.string.display_save_result_msg,
                    it.getOrNull()?.name,
                    it.getOrNull()?.tracks?.size
                )
            } else {
                app.getString(
                    R.string.display_save_error_msg,
                    it.exceptionOrNull()?.message
                )
            }
        }.map { LiveEvent(it) }

    fun onConfirmSave(playlistName: String) {
        inputPlaylistName.value = playlistName
    }
}
