package me.cpele.baladr

data class TrackDto(
    val id: String?,
    val name: String?,
    val album: AlbumDto?,
    val artists: List<ArtistDto?>?,
    val duration_ms: Long?
)