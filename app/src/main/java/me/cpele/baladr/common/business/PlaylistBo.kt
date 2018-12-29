package me.cpele.baladr.common.business

data class PlaylistBo(
    val id: Int,
    val name: String,
    val tracks: List<TrackBo>,
    val uri: String
)