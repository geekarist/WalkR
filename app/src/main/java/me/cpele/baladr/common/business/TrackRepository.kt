package me.cpele.baladr.common.business

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import me.cpele.baladr.common.database.TrackDao

class TrackRepository(private val trackDao: TrackDao) {
    fun findByTempo(tempo: Int): LiveData<List<TrackBo>> =
        Transformations.map(trackDao.findByTempo(tempo)) { entities ->
            entities.map {
                TrackBo(
                    it.id,
                    it.cover,
                    it.title,
                    it.artist,
                    it.duration,
                    it.tempo
                )
            }
        }
}
