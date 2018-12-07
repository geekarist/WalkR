package me.cpele.baladr.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.cpele.baladr.common.database.PlaylistWithTracksDao

class LibraryViewModel(playlistWithTracksDao: PlaylistWithTracksDao) : ViewModel() {

    class Factory(private val playlistWithTracksDao: PlaylistWithTracksDao) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass.cast(LibraryViewModel(playlistWithTracksDao)) as T
        }
    }

    val playlists = playlistWithTracksDao.findAll()
}
