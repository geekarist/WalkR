package me.cpele.baladr.common.business

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.kittinunf.fuel.Fuel
import com.github.musichin.reactivelivedata.combineLatestWith
import com.github.musichin.reactivelivedata.switchMap
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.cpele.baladr.common.AsyncTransform
import me.cpele.baladr.common.database.*
import me.cpele.baladr.common.datasource.PlaylistDto

class PlaylistRepository(
    private val playlistDao: PlaylistDao,
    private val playlistTrackDao: PlaylistTrackDao,
    private val joinPlaylistTrackDao: JoinPlaylistTrackDao,
    private val accessTokenDao: AccessTokenDao,
    private val gson: Gson
) {
    fun findAll(): LiveData<List<PlaylistBo>> {
        return AsyncTransform.map(joinPlaylistTrackDao.findAll()) { joinResult: List<JoinPlaylistTrackWrapper>? ->
            joinResult
                ?.groupBy { it: JoinPlaylistTrackWrapper -> it.playlist }
                ?.map { entry: Map.Entry<PlaylistEntity, List<JoinPlaylistTrackWrapper>> ->
                    with(entry) {
                        PlaylistBo(
                            key.plId,
                            key.plName,
                            value.map { groupedJoinResult: JoinPlaylistTrackWrapper ->
                                with(groupedJoinResult.track) {
                                    TrackBo(
                                        trId,
                                        trCover,
                                        trTitle,
                                        trArtist,
                                        trDuration,
                                        trTempo
                                    )
                                }
                            })
                    }
                }
        }
    }

    fun insert(playlist: PlaylistBo): LiveData<Result<PlaylistBo>> =
        insertEntitiesAsync(playlist)
            .combineLatestWith(accessTokenDao.get()) { result: Result<PlaylistBo>, accessToken: String? ->
                Pair(result, accessToken)
            }.switchMap {
                val (result: Result<PlaylistBo>, accessToken: String?) = it
                val resultData = MutableLiveData<Result<PlaylistBo>>()
                GlobalScope.launch {
                    when {
                        result.isSuccess && accessToken != null -> {
                            try {
                                insertResource(playlist, accessToken)
                                resultData.postValue(Result.success(playlist))
                            } catch (e: Exception) {
                                resultData.postValue(Result.failure(e))
                            }
                        }
                        result.isSuccess && accessToken == null -> {
                            resultData.postValue(
                                Result.failure(
                                    Exception(
                                        "Error inserting playlist: access token is not set"
                                    )
                                )
                            )
                        }
                        result.isFailure -> {
                            resultData.postValue(result)
                        }
                    }
                }
                resultData
            }

    private fun insertEntitiesAsync(playlist: PlaylistBo): LiveData<Result<PlaylistBo>> {
        val resultData = MutableLiveData<Result<PlaylistBo>>()
        GlobalScope.launch {
            try {
                insertEntities(playlist)
            } catch (e: Exception) {
                resultData.postValue(Result.failure(e))
            }
            resultData.postValue(Result.success(playlist))
        }
        return resultData
    }

    private fun insertResource(playlist: PlaylistBo, accessToken: String) {
        val (_, _, result) = Fuel.post("https://api.spotify.com/v1/me/playlists")
            .header(
                mapOf(
                    "Authorization" to "Bearer $accessToken",
                    "Content-Type" to "application/json"
                )
            ).body(gson.toJson(PlaylistDto(name = playlist.name)))
            .response()

        if (result is com.github.kittinunf.result.Result.Failure) throw result.getException()
    }

    private fun insertEntities(playlist: PlaylistBo) {
        val playlistEntity = PlaylistEntity(plName = playlist.name)
        val insertedPlaylistId = playlistDao.insert(playlistEntity)

        // We know that playlist.tracks come from the db so we don't need to insert them
        val playlistTrackEntities = playlist.tracks.map {
            PlaylistTrackEntity(insertedPlaylistId, it.id)
        }
        playlistTrackDao.insertAll(playlistTrackEntities)
    }
}
