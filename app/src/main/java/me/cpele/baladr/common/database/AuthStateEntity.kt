package me.cpele.baladr.common.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AuthStateEntity(@PrimaryKey val asId: Long, val asValue: String)