package me.cpele.baladr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class LibraryViewModel(playlistDao: PlaylistDao) : ViewModel() {

    class Factory(private val playlistDao: PlaylistDao) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass.cast(LibraryViewModel(playlistDao)) as T
        }
    }

    val playlists = playlistDao.findAll()
}
