package me.cpele.baladr.common.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TrackEntity(

    @PrimaryKey
    val id: String,

    val cover: String,
    val title: String,
    val artist: String,
    val duration: String,
    val tempo: Int
)