package me.cpele.baladr

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.cpele.baladr.common.business.PlaylistRepository
import me.cpele.baladr.common.business.TrackRepository
import me.cpele.baladr.common.database.CustomDatabase
import me.cpele.baladr.common.database.DatabasePopulationCallback
import me.cpele.baladr.feature.library.LibraryViewModel
import me.cpele.baladr.feature.playlistdisplay.PlaylistDisplayViewModel
import me.cpele.baladr.feature.playlistgeneration.AndroidTempoDetection
import me.cpele.baladr.feature.playlistgeneration.PlaylistGenerationViewModel
import me.cpele.baladr.feature.playlistgeneration.TempoDetection
import net.openid.appauth.AuthorizationService

class CustomApp : Application() {

    val database: CustomDatabase by lazy {
        Room.databaseBuilder(this, CustomDatabase::class.java, "custom.db")
            .addCallback(DatabasePopulationCallback())
            .build()
    }

    private val gson: Gson by lazy { Gson() }

    private val authService by lazy { AuthorizationService(this) }

    val authStateRepository by lazy { AuthStateRepository(database.authStateDao()) }

    private val playlistRepository: PlaylistRepository by lazy {
        PlaylistRepository(
            database.playlistDao(),
            database.playlistTrackDao(),
            database.joinPlaylistTrackDao(),
            authStateRepository,
            gson,
            authService
        )
    }

    private val trackRepository: TrackRepository by lazy {
        TrackRepository(database.trackDao())
    }

    val playlistDisplayViewModelFactory: ViewModelProvider.Factory by lazy {
        PlaylistDisplayViewModel.Factory(
            this,
            trackRepository,
            playlistRepository
        )
    }

    val libraryViewModelFactory: ViewModelProvider.Factory by lazy {
        LibraryViewModel.Factory(this, playlistRepository)
    }

    val mainViewModelFactory by lazy {
        MainViewModel.Factory(authStateRepository)
    }

    private val tempoDetection: TempoDetection by lazy { AndroidTempoDetection(this) }

    val playlistGenerationViewModelFactory: ViewModelProvider.Factory by lazy {
        PlaylistGenerationViewModel.Factory(tempoDetection)
    }

    companion object {
        lateinit var instance: CustomApp
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        StethoWrapper.initializeWithDefaults(this)
        triggerDbInit()
    }

    private fun triggerDbInit() = GlobalScope.launch {
        database.trackDao().countSync()
    }
}
