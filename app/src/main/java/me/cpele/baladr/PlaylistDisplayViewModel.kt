package me.cpele.baladr

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class PlaylistDisplayViewModel(application: Application) : AndroidViewModel(application) {
    val tracks: LiveData<List<Track>> = MutableLiveData<List<Track>>().apply { value = listOf(Track("1"), Track("2")) }
}
