package me.cpele.baladr

import androidx.room.Embedded
import androidx.room.Relation

data class PlaylistWithTracksBo(@Embedded val playlist: PlaylistBo) {
    @Relation(entityColumn = "playlistId", parentColumn = "id")
    var tracks: List<TrackBo>? = null
}