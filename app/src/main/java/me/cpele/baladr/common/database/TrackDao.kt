package me.cpele.baladr.common.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TrackDao {
    @Query("SELECT * FROM TrackEntity WHERE tempo = :tempo")
    fun findByTempo(tempo: Int): LiveData<List<TrackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(tracks: List<TrackEntity>)

    @Query("SELECT COUNT(*) FROM TrackEntity")
    fun countSync(): Int

    @Query("SELECT * FROM TrackEntity WHERE id = :id")
    fun findOneSync(id: String): TrackEntity
}
