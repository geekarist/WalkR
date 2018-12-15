package me.cpele.baladr.common.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlaylistTrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(playlistTrackEntities: List<PlaylistTrackEntity>)

    @Query("SELECT * FROM PlaylistTrackEntity WHERE ptPlaylistId = :id")
    fun findByPlaylistIdSync(id: Int): List<PlaylistTrackEntity>
}
