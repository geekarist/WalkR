package me.cpele.baladr

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = PlaylistBo::class,
        parentColumns = ["id"],
        childColumns = ["playlistId"]
    )]
)
data class TrackBo(

    @PrimaryKey
    val id: String,

    val cover: String,
    val title: String,
    val artist: String,
    val duration: String,
    val tempo: Int,

    var playlistId: Int? = null
)