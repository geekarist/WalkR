package me.cpele.baladr

import android.app.Application
import android.view.View
import androidx.lifecycle.*

class PlaylistDisplayViewModel(application: Application, trackDao: TrackDao) : AndroidViewModel(application) {

    class Factory(
        private val application: Application,
        private val trackDao: TrackDao
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass.cast(PlaylistDisplayViewModel(application, trackDao)) as T
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

    fun onClickSave() {
        TODO()
    }

    fun onConnectivityChange(status: Boolean) {
        connectivityStatusData.postValue(status)
    }
}
