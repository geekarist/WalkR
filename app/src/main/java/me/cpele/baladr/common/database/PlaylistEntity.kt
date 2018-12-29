package me.cpele.baladr.common.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val plId: Int = 0,
    val plName: String,
    val plUri: String
)
