package me.cpele.baladr.common.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
interface JoinPlaylistTrackDao {
    @Query(
        """
        SELECT p.*, t.*, pt.* FROM PlaylistEntity p
        INNER JOIN PlaylistTrackEntity pt ON p.plId = pt.ptPlaylistId
        INNER JOIN TrackEntity t ON t.trId = pt.ptTrackId
        """
    )
    fun findAll(): LiveData<List<JoinPlaylistTrackWrapper>>
}
