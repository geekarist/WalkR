package me.cpele.baladr.feature.library.item

data class GroupLibItem(val value: String) : LibItem() {
    override fun hasSameContents(newItem: LibItem): Boolean {
        val newGroupItem = newItem as? GroupLibItem
        return this == newGroupItem
    }
}
