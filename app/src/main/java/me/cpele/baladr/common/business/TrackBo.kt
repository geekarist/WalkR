package me.cpele.baladr.common.business

data class TrackBo(
    val id: String,
    val cover: String,
    val title: String,
    val artist: String,
    val duration: String,
    val tempo: Int,
    val uri: String
)
