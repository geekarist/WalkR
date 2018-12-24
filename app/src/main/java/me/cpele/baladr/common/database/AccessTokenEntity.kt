package me.cpele.baladr.common.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AccessTokenEntity(@PrimaryKey val atId: Long, val atValue: String)