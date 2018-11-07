package me.cpele.baladr

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.*

class PlaylistDisplayViewModel(application: Application) : AndroidViewModel(application) {
    val tracks: LiveData<List<Track>> = MutableLiveData<List<Track>>().apply {
        value = listOf(
            Track(
                UUID.randomUUID().toString(),
                "https://picsum.photos/100?random",
                "Lorem Ipsum",
                "Dolor Sit",
                "12:34"
            ),
            Track(
                UUID.randomUUID().toString(),
                "https://picsum.photos/100?random",
                "Persius Noluisse",
                "Reprehendunt Eam",
                "56:78"
            )
        )
    }
}
