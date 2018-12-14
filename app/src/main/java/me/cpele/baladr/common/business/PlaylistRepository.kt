package me.cpele.baladr.common.business

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.cpele.baladr.common.database.*

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
                val trackBos = trackEntities.map {
                    TrackBo(
                        it.id,
                        it.cover,
                        it.title,
                        it.artist,
                        it.duration,
                        it.tempo
                    )
                }
                PlaylistBo(playlistEntity.id, playlistEntity.name, trackBos)
            }
            result.postValue(playlistBos)
        }
        return result
    }

    fun insert(playlist: PlaylistBo) = GlobalScope.launch {
        val playlistEntity = PlaylistEntity(name = playlist.name)
        val insertedPlaylistId = playlistDao.insert(playlistEntity)

        val trackEntities = playlist.tracks.map {
            TrackEntity(it.id, it.cover, it.title, it.artist, it.duration, it.tempo)
        }
        trackDao.insertAll(trackEntities)

        val playlistTrackEntities = trackEntities.map {
            PlaylistTrackEntity(insertedPlaylistId, it.id)
        }
        playlistTrackDao.insertAll(playlistTrackEntities)
    }
}
