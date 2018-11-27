package me.cpele.baladr

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity
@TypeConverters(ListTypeConverter::class)
data class PlaylistBo(@PrimaryKey val id: String, val trackIds: List<String>)
