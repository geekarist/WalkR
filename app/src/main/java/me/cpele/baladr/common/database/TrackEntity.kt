package me.cpele.baladr.common.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TrackEntity(

    @PrimaryKey
    val trId: String,

    val trCover: String,
    val trTitle: String,
    val trArtist: String,
    val trDuration: String,
    val trTempo: Int,
    val trUri: String
)