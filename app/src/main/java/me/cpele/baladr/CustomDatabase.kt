package me.cpele.baladr

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    version = 1, entities = [
        Track::class
    ]
)
abstract class CustomDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
}
