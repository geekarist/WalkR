package me.cpele.baladr.common.database

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    primaryKeys = ["ptPlaylistId", "ptTrackId"],
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            childColumns = ["ptPlaylistId"],
            parentColumns = ["plId"]
        ),
        ForeignKey(
            entity = TrackEntity::class,
            childColumns = ["ptTrackId"],
            parentColumns = ["trId"]
        )
    ]
)
data class PlaylistTrackEntity(val ptPlaylistId: Long, val ptTrackId: String)