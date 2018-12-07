package me.cpele.baladr

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
interface PlaylistWithTracksDao {
    @Query("SELECT * FROM PlaylistBo")
    fun findAll(): LiveData<List<PlaylistWithTracksBo>>
}
