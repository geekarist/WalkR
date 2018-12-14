package me.cpele.baladr.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.cpele.baladr.common.business.PlaylistRepository

class LibraryViewModel(playlistRepository: PlaylistRepository) : ViewModel() {

    class Factory(private val playlistRepository: PlaylistRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass.cast(LibraryViewModel(playlistRepository)) as T
        }
    }

    val playlists = playlistRepository.findAll()
}
