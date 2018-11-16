package me.cpele.baladr

import androidx.lifecycle.LiveData
import androidx.room.Dao

@Dao
interface TrackDao {
    fun findByTempo(tempo: Int): LiveData<List<Track>>
}
