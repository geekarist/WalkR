package me.cpele.baladr.common.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
interface PlaylistWithTracksDao {
    @Query("SELECT * FROM PlaylistEntity")
    fun findAll(): LiveData<List<PlaylistWithTracksEntity>>
}
