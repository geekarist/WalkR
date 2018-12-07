package me.cpele.baladr.common.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TrackDao {
    @Query("SELECT * FROM TrackBo WHERE tempo = :tempo")
    fun findByTempo(tempo: Int): LiveData<List<TrackBo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(tracks: List<TrackBo>)

    @Query("SELECT COUNT(*) FROM TrackBo")
    fun count(): Int
}
