package me.cpele.baladr.feature.library.item

import me.cpele.baladr.common.business.PlaylistBo

data class PlaylistLibItem(val value: PlaylistBo) : LibItem() {
    override fun hasSameContents(newItem: LibItem): Boolean {
        val newPlaylistItem = newItem as? PlaylistLibItem
        return this == newPlaylistItem
    }
}
