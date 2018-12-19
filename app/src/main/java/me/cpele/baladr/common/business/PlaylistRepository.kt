package me.cpele.baladr.common.business

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    fun insert(playlist: PlaylistBo): LiveData<Result<Unit>> {

        val resultData = MutableLiveData<Result<Unit>>()

        GlobalScope.launch {

            try {
                val playlistEntity = PlaylistEntity(plName = playlist.name)
                val insertedPlaylistId = playlistDao.insert(playlistEntity)

                // We know that playlist.tracks come from the db so we don't need to insert them
                val playlistTrackEntities = playlist.tracks.map {
                    PlaylistTrackEntity(insertedPlaylistId, it.id)
                }
                playlistTrackDao.insertAll(playlistTrackEntities)

                val (_, _, result) = Fuel.post("https://api.spotify.com/v1/me/playlists")
                    .header(
                        mapOf(
                            "Authorization" to "Bearer BQBS2oOuyDowgtNCB2yQoNLM1_XzOoTNiByI00hDCT1Hrrxy7Ye2dIVo786sD0xLXSoVNnt-AXgbgBjvahhl7lNlVebG0Bly64wkgg5GrIDRX7r3FFOPN4c2fHTfqPTt47epB3GAM_KtJDskOciOkP6CdnSREK14bD8VeWkC4hp2G9M4Nl5G5yvprWtRLr6_oXfXRNIg8M9h1mwd9igRYisprJE2Hw",
                            "Content-Type" to "application/json"
                        )
                    ).body(gson.toJson(PlaylistDto(name = playlist.name)))
                    .response()

                resultData.postValue(
                    when (result) {
                        is com.github.kittinunf.result.Result.Success -> Result.success(Unit)
                        is com.github.kittinunf.result.Result.Failure -> Result.failure(result.getException())
                    }
                )
            } catch (e: Exception) {
                resultData.postValue(Result.failure(e))
            }
        }
        return resultData
    }
}
