package me.cpele.baladr.common.business

import androidx.lifecycle.LiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.cpele.baladr.common.AsyncTransform
import me.cpele.baladr.common.database.*

class PlaylistRepository(
    private val playlistDao: PlaylistDao,
    private val trackDao: TrackDao,
    private val playlistTrackDao: PlaylistTrackDao,
    private val joinPlaylistTrackDao: JoinPlaylistTrackDao
) {
    fun findAll(): LiveData<List<PlaylistBo>> =
        AsyncTransform.map(joinPlaylistTrackDao.findAll()) { joinResult: List<JoinPlaylistTrackWrapper>? ->
            joinResult
                ?.groupBy { it: JoinPlaylistTrackWrapper -> it.playlist }
                ?.map { entry: Map.Entry<PlaylistEntity, List<JoinPlaylistTrackWrapper>> ->
                    PlaylistBo(
                        entry.key.plId,
                        entry.key.plName,
                        entry.value.map { groupedJoinResult: JoinPlaylistTrackWrapper ->
                            TrackBo(
                                groupedJoinResult.track.trId,
                                groupedJoinResult.track.trCover,
                                groupedJoinResult.track.trTitle,
                                groupedJoinResult.track.trArtist,
                                groupedJoinResult.track.trDuration,
                                groupedJoinResult.track.trTempo
                            )
                        })
                }
        }

    fun insert(playlist: PlaylistBo) = GlobalScope.launch {
        val playlistEntity = PlaylistEntity(plName = playlist.name)
        val insertedPlaylistId = playlistDao.insert(playlistEntity)

        val trackEntities = playlist.tracks.map {
            TrackEntity(it.id, it.cover, it.title, it.artist, it.duration, it.tempo)
        }
        trackDao.insertAll(trackEntities)

        val playlistTrackEntities = trackEntities.map {
            PlaylistTrackEntity(insertedPlaylistId, it.trId)
        }
        playlistTrackDao.insertAll(playlistTrackEntities)
    }
}
