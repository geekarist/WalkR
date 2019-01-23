package me.cpele.baladr.common.business

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.kittinunf.fuel.Fuel
import com.github.musichin.reactivelivedata.switchMap
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.cpele.baladr.AuthStateRepository
import me.cpele.baladr.BuildConfig
import me.cpele.baladr.common.AsyncTransform
import me.cpele.baladr.common.database.*
import me.cpele.baladr.common.datasource.PlaylistDto
import net.openid.appauth.AuthorizationService
import net.openid.appauth.ClientSecretBasic
import java.nio.charset.Charset

class PlaylistRepository(
    private val playlistDao: PlaylistDao,
    private val playlistTrackDao: PlaylistTrackDao,
    private val joinPlaylistTrackDao: JoinPlaylistTrackDao,
    private val authStateRepository: AuthStateRepository,
    private val gson: Gson,
    private val authService: AuthorizationService
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
                                        trTempo,
                                        trUri
                                    )
                                }
                            },
                            key.plUri
                        )
                    }
                }
        }
    }

    fun insert(playlist: PlaylistBo): LiveData<Result<PlaylistBo>> =
        authStateRepository.get().switchMap { authState ->
            val resultData = MutableLiveData<Result<PlaylistBo>>()
            GlobalScope.launch {
                val playlistWithId = insertEntities(playlist)
                if (authState != null) {
                    authState.needsTokenRefresh = true
                    val clientAuth = ClientSecretBasic(BuildConfig.SPOTIFY_CLIENT_SECRET)
                    authState.performActionWithFreshTokens(authService, clientAuth) { accessToken, _, ex ->
                        try {
                            resultData.postValue(
                                when {
                                    ex != null -> Result.failure(Exception("Error inserting resource when performing auth action"))
                                    accessToken != null -> {
                                        val playlistWithUri = insertResource(playlistWithId, accessToken)
                                        updateEntities(playlistWithUri)
                                        Result.success(playlistWithUri)
                                    }
                                    else -> Result.failure(Exception("Error inserting resource: no access token"))
                                }
                            )
                        } catch (e: Exception) {
                            resultData.postValue(Result.failure(e))
                        }
                    }
                } else {
                    resultData.postValue(Result.failure(Exception("Error inserting playlist: not authorized")))
                }
            }
            resultData
        }

    private fun insertResource(playlist: PlaylistBo, accessToken: String): PlaylistBo {
        val (_, _, insertionResult) = Fuel.post("https://api.spotify.com/v1/me/playlists")
            .header(
                mapOf(
                    "Authorization" to "Bearer $accessToken",
                    "Content-Type" to "application/json"
                )
            ).body(gson.toJson(PlaylistDto(name = playlist.name)))
            .response()

        if (insertionResult is com.github.kittinunf.result.Result.Failure) throw insertionResult.getException()

        val insertedPlaylistDto =
            gson.fromJson(
                insertionResult.get().toString(Charset.defaultCharset()),
                PlaylistDto::class.java
            )

        val playlistExternalId = insertedPlaylistDto.id
        val uriList = playlist.tracks.map { it.uri }
        val joinedUris = uriList.joinToString(",")

        val (_, _, updateResult) =
                Fuel.post("https://api.spotify.com/v1/playlists/$playlistExternalId/tracks?uris=$joinedUris")
                    .header(
                        mapOf(
                            "Authorization" to "Bearer $accessToken",
                            "Content-Type" to "application/json"
                        )
                    ).response()

        if (updateResult is com.github.kittinunf.result.Result.Failure) throw updateResult.getException()

        return playlist.copy(uri = insertedPlaylistDto.uri ?: TODO())
    }

    private fun insertEntities(playlist: PlaylistBo): PlaylistBo {
        val playlistEntity = PlaylistEntity(plName = playlist.name, plUri = playlist.uri)
        val insertedPlaylistId = playlistDao.insert(playlistEntity)

        // We know that playlist.tracks come from the db so we don't need to insert them
        val playlistTrackEntities = playlist.tracks.map {
            PlaylistTrackEntity(insertedPlaylistId, it.id)
        }
        playlistTrackDao.insertAll(playlistTrackEntities)

        return playlist.copy(id = insertedPlaylistId)
    }

    private fun updateEntities(playlist: PlaylistBo) {
        val playlistEntity = PlaylistEntity(
            plId = playlist.id,
            plName = playlist.name,
            plUri = playlist.uri
        )
        playlistDao.update(playlistEntity)
    }
}
