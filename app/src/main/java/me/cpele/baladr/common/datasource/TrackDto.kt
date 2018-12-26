package me.cpele.baladr.common.datasource

data class TrackDto(
    val id: String?,
    val name: String?,
    val album: AlbumDto?,
    val artists: List<ArtistDto?>?,
    val duration_ms: Long?,
    val uri: String?
)