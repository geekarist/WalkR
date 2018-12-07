package me.cpele.baladr.feature.library

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import me.cpele.baladr.R
import me.cpele.baladr.common.database.PlaylistWithTracksBo

class LibraryAdapter : ListAdapter<PlaylistWithTracksBo, PlaylistViewHolder>(DiffCallback) {

    object DiffCallback : DiffUtil.ItemCallback<PlaylistWithTracksBo>() {
        override fun areItemsTheSame(
            oldItem: PlaylistWithTracksBo,
            newItem: PlaylistWithTracksBo
        ): Boolean = oldItem.playlist.id == newItem.playlist.id

        override fun areContentsTheSame(
            oldItem: PlaylistWithTracksBo,
            newItem: PlaylistWithTracksBo
        ): Boolean = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.view_library_playlist, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
