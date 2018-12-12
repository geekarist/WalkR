package me.cpele.baladr.common.database

import androidx.room.Embedded
import androidx.room.Relation

data class PlaylistWithTracksEntity(@Embedded val playlist: PlaylistEntity) {
    @Relation(entityColumn = "playlistId", parentColumn = "id")
    var tracks: List<TrackEntity>? = null
}