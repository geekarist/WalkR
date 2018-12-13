package me.cpele.baladr.feature.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.cpele.baladr.common.business.PlaylistBo
import me.cpele.baladr.common.business.TrackBo
import me.cpele.baladr.common.database.PlaylistDao
import me.cpele.baladr.common.database.PlaylistTrackDao
import me.cpele.baladr.common.database.TrackDao

class PlaylistRepository(
    private val playlistDao: PlaylistDao,
    private val trackDao: TrackDao,
    private val playlistTrackDao: PlaylistTrackDao
) {
    fun findAll(): LiveData<List<PlaylistBo>> {
        val result = MutableLiveData<List<PlaylistBo>>()
        GlobalScope.launch {
            val playlistBos = playlistDao.findAllSync().map { playlistEntity ->
                val playlistTrackEntities = playlistTrackDao.findByPlaylistIdSync(playlistEntity.id)
                val trackEntities = playlistTrackEntities.map { ptEntity ->
                    trackDao.findOneSync(ptEntity.trackId)
                }
                val trackBos = trackEntities.map { TrackBo(it.id, it.cover) }
                PlaylistBo(playlistEntity.id, playlistEntity.name, trackBos)
            }
            result.postValue(playlistBos)
        }
        return result
    }
}
