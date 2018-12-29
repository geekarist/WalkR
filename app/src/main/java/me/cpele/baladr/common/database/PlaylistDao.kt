package me.cpele.baladr.common.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PlaylistDao {
    @Insert
    fun insert(playlist: PlaylistEntity): Long

    @Update
    fun update(playlist: PlaylistEntity)

    @Query("SELECT * FROM PlaylistEntity")
    fun findAll(): LiveData<List<PlaylistEntity>>

    @Query("SELECT * FROM PlaylistEntity")
    fun findAllSync(): List<PlaylistEntity>
}
