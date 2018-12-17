package me.cpele.baladr.common.business

import androidx.lifecycle.LiveData
import com.github.kittinunf.fuel.Fuel
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.cpele.baladr.common.AsyncTransform
import me.cpele.baladr.common.database.*
import me.cpele.baladr.common.datasource.PlaylistDto

class PlaylistRepository(
    private val playlistDao: PlaylistDao,
    private val trackDao: TrackDao,
    private val playlistTrackDao: PlaylistTrackDao,
    private val joinPlaylistTrackDao: JoinPlaylistTrackDao,
    private val gson: Gson
) {
    fun findAll(): LiveData<List<PlaylistBo>> {
        return AsyncTransform.map(joinPlaylistTrackDao.findAll()) { joinResult: List<JoinPlaylistTrackWrapper>? ->
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

        val (request, response, result) = Fuel.post("https://api.spotify.com/v1/me/playlists")
            .header(
                mapOf(
                    "Authorization" to "Bearer BQBS2oOuyDowgtNCB2yQoNLM1_XzOoTNiByI00hDCT1Hrrxy7Ye2dIVo786sD0xLXSoVNnt-AXgbgBjvahhl7lNlVebG0Bly64wkgg5GrIDRX7r3FFOPN4c2fHTfqPTt47epB3GAM_KtJDskOciOkP6CdnSREK14bD8VeWkC4hp2G9M4Nl5G5yvprWtRLr6_oXfXRNIg8M9h1mwd9igRYisprJE2Hw",
                    "Content-Type" to "application/json"
                )
            ).body(gson.toJson(PlaylistDto(name = playlist.name)))
            .response()
    }
}
