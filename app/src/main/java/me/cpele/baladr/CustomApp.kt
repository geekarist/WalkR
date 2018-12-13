package me.cpele.baladr

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.cpele.baladr.common.database.CustomDatabase
import me.cpele.baladr.common.database.DatabasePopulationCallback
import me.cpele.baladr.feature.library.LibraryViewModel
import me.cpele.baladr.feature.library.PlaylistRepository
import me.cpele.baladr.feature.playlist_display.PlaylistDisplayViewModel

class CustomApp : Application() {

    val database: CustomDatabase by lazy {
        Room.databaseBuilder(this, CustomDatabase::class.java, "custom.db")
            .addCallback(DatabasePopulationCallback())
            .build()
    }

    private val playlistRepository: PlaylistRepository by lazy {
        PlaylistRepository(
            database.playlistDao(),
            database.trackDao(),
            database.playlistTrackDao()
        )
    }

    val playlistDisplayViewModelFactory: ViewModelProvider.Factory by lazy {
        PlaylistDisplayViewModel.Factory(
            this,
            database.trackDao(),
            database.playlistDao(),
            database.playlistTrackDao()
        )
    }

    val libraryViewModelFactory: ViewModelProvider.Factory by lazy {
        LibraryViewModel.Factory(playlistRepository)
    }

    companion object {
        lateinit var instance: CustomApp
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        triggerDbInit()
    }

    private fun triggerDbInit() = GlobalScope.launch {
        database.trackDao().countSync()
    }
}
