package me.cpele.baladr.feature.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class PlaylistRepository {
    fun findAll(): LiveData<List<PlaylistBo>> {
        return MutableLiveData<List<PlaylistBo>>().apply {
            value = listOf(
                PlaylistBo(
                    1, "yo", listOf(
                        TrackBo(1, "https://i.scdn.co/image/e4d8363405f2093b1e16bb129b2246852e8911a2"),
                        TrackBo(2, "https://i.scdn.co/image/7317e854667da26446576a747d1efe8d9d58e92d"),
                        TrackBo(3, "https://i.scdn.co/image/5acb2a5b069c2f14f6e7efd5daa9bd340131ca47"),
                        TrackBo(4, "https://i.scdn.co/image/79ef8fb6e772a47e3e045a747376dca7b900583e")
                    )
                )
            )
        }
    }
}
