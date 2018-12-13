package me.cpele.baladr.common.database

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    primaryKeys = ["playlistId", "trackId"],
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            childColumns = ["playlistId"],
            parentColumns = ["id"]
        ),
        ForeignKey(
            entity = TrackEntity::class,
            childColumns = ["trackId"],
            parentColumns = ["id"]
        )
    ]
)
data class PlaylistTrackEntity(val playlistId: Long, val trackId: String)