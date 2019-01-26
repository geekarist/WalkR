package me.cpele.baladr.common.business

import java.util.*

data class PlaylistBo(
    val id: Long,
    val name: String,
    val tracks: List<TrackBo>,
    val uri: String? = null,
    val date: Date
)