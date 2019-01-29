package me.cpele.baladr.feature.library.holder

import android.view.View
import kotlinx.android.synthetic.main.view_lib_item_group.view.*
import me.cpele.baladr.feature.library.item.GroupLibItem
import me.cpele.baladr.feature.library.item.LibItem

class GroupLibItemViewHolder(view: View) : LibItemViewHolder(view) {
    override fun bind(item: LibItem) {
        val groupItem = item as? GroupLibItem
        groupItem?.apply { itemView.libItemGroupTitle.text = value }
    }
}
