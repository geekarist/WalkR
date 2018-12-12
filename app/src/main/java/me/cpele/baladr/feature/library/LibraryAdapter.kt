package me.cpele.baladr.feature.library

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import me.cpele.baladr.R
import me.cpele.baladr.common.database.PlaylistWithTracksEntity

class LibraryAdapter : ListAdapter<PlaylistWithTracksEntity, PlaylistViewHolder>(DiffCallback) {

    object DiffCallback : DiffUtil.ItemCallback<PlaylistWithTracksEntity>() {
        override fun areItemsTheSame(
            oldItem: PlaylistWithTracksEntity,
            newItem: PlaylistWithTracksEntity
        ): Boolean = oldItem.playlist.id == newItem.playlist.id

        override fun areContentsTheSame(
            oldItem: PlaylistWithTracksEntity,
            newItem: PlaylistWithTracksEntity
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
