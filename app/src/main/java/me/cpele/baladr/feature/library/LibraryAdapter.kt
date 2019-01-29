package me.cpele.baladr.feature.library

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import me.cpele.baladr.R
import me.cpele.baladr.feature.library.holder.GroupLibItemViewHolder
import me.cpele.baladr.feature.library.holder.LibItemViewHolder
import me.cpele.baladr.feature.library.holder.PlaylistLibItemViewHolder
import me.cpele.baladr.feature.library.item.GroupLibItem
import me.cpele.baladr.feature.library.item.LibItem
import me.cpele.baladr.feature.library.item.PlaylistLibItem

class LibraryAdapter : ListAdapter<LibItem, LibItemViewHolder>(DiffCallback) {

    object DiffCallback : DiffUtil.ItemCallback<LibItem>() {
        override fun areItemsTheSame(
            oldItem: LibItem,
            newItem: LibItem
        ): Boolean = oldItem === newItem

        override fun areContentsTheSame(
            oldItem: LibItem,
            newItem: LibItem
        ): Boolean = oldItem.hasSameContents(newItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ItemType.PLAYLIST.ordinal -> {
                val view = inflater.inflate(R.layout.view_lib_item_playlist, parent, false)
                PlaylistLibItemViewHolder(view)
            }
            ItemType.GROUP.ordinal -> {
                val view = inflater.inflate(R.layout.view_lib_item_group, parent, false)
                GroupLibItemViewHolder(view)
            }
            else -> throw IllegalStateException("Unknown item type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: LibItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return when (item) {
            is PlaylistLibItem -> ItemType.PLAYLIST.ordinal
            is GroupLibItem -> ItemType.GROUP.ordinal
            else -> throw IllegalStateException("Unknown item type: ${item.javaClass.simpleName}")
        }
    }

    enum class ItemType {
        PLAYLIST, GROUP
    }
}
