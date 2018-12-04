package me.cpele.baladr

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PlaylistBo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)
