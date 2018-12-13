package me.cpele.baladr.common.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    version = 1,
    entities = [
        TrackEntity::class,
        PlaylistEntity::class,
        PlaylistTrackEntity::class
    ]
)
abstract class CustomDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistWithTracksDao(): PlaylistWithTracksDao
    abstract fun playlistTrackDao(): PlaylistTrackDao
}
