package me.cpele.baladr

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room

class CustomApp : Application() {

    lateinit var playlistDisplayViewModelFactory: ViewModelProvider.Factory

    companion object {
        lateinit var instance: CustomApp
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        val database: CustomDatabase = Room.databaseBuilder(this, CustomDatabase::class.java, "custom.db").build()
        playlistDisplayViewModelFactory = PlaylistDisplayViewModel.Factory(this, database.trackDao())
    }
}
