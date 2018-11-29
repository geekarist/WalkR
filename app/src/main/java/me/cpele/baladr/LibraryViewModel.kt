package me.cpele.baladr

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LibraryViewModel : ViewModel() {
    val playlists = MutableLiveData<List<PlaylistBo>>().apply {
        value = listOf(
            PlaylistBo(0, "Playlist 1", listOf("a", "b", "c")),
            PlaylistBo(1, "Playlist 2", listOf("d", "e", "f"))
        )
    }
}
