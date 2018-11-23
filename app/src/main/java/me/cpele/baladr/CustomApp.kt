package me.cpele.baladr

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CustomApp : Application() {

    val database: CustomDatabase by lazy {
        Room.databaseBuilder(this, CustomDatabase::class.java, "custom.db")
            .addCallback(DatabasePopulationCallback())
            .build()
    }

    val playlistDisplayViewModelFactory: ViewModelProvider.Factory by lazy {
        PlaylistDisplayViewModel.Factory(this, database.trackDao())
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
        database.trackDao().count()
    }
}
