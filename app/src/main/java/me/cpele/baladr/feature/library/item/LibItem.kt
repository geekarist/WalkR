package me.cpele.baladr.feature.library.item

abstract class LibItem {
    abstract fun hasSameContents(newItem: LibItem): Boolean
}
