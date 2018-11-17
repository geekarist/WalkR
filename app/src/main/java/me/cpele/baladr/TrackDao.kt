package me.cpele.baladr

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
interface TrackDao {
    @Query("SELECT * FROM Track WHERE tempo = :tempo")
    fun findByTempo(tempo: Int): LiveData<List<Track>>
}
