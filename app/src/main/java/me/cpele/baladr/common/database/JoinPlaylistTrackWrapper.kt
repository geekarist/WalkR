package me.cpele.baladr.common.database

import androidx.room.Embedded

data class JoinPlaylistTrackWrapper(
    @Embedded
    val playlist: PlaylistEntity,
    @Embedded
    val track: TrackEntity,
    @Embedded
    val playlistTrack: PlaylistTrackEntity
)