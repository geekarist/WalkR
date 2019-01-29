package me.cpele.baladr.feature.library.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import me.cpele.baladr.feature.library.item.LibItem

abstract class LibItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(item: LibItem)
}
