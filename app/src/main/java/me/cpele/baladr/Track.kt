package me.cpele.baladr

import androidx.room.Entity

@Entity
data class Track(
    val id: String,
    val cover: String,
    val title: String,
    val artist: String,
    val duration: String
)