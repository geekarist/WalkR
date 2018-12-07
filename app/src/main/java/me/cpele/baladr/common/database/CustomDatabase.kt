package me.cpele.baladr.common.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    version = 1,
    entities = [
        TrackBo::class,
        PlaylistBo::class
    ]
)
abstract class CustomDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistWithTracksDao(): PlaylistWithTracksDao
}
