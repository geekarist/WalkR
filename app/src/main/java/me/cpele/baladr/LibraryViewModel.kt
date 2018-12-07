package me.cpele.baladr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class LibraryViewModel(playlistWithTracksDao: PlaylistWithTracksDao) : ViewModel() {

    class Factory(private val playlistWithTracksDao: PlaylistWithTracksDao) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass.cast(LibraryViewModel(playlistWithTracksDao)) as T
        }
    }

    val playlists = playlistWithTracksDao.findAll()
}
