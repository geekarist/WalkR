package me.cpele.baladr.common.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PlaylistBo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String
)
