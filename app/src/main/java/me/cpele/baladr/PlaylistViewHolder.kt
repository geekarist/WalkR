package me.cpele.baladr

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.view_library_playlist.view.*

class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(item: PlaylistWithTracksBo?) {
        item?.apply {
            itemView.playlistItemName.text = playlist.name
            itemView.playlistItemTrackCount.text = tracks?.size.toString()
        }
    }
}
