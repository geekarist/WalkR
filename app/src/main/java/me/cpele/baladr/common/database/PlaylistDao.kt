package me.cpele.baladr.common.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PlaylistDao {
    @Insert
    fun insert(playlist: PlaylistBo): Long

    @Query("SELECT * FROM PlaylistBo")
    fun findAll(): LiveData<List<PlaylistBo>>
}
