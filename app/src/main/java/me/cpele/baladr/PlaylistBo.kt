package me.cpele.baladr

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity
@TypeConverters(ListTypeConverter::class)
data class PlaylistBo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val trackIds: List<String>
)
