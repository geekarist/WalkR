package me.cpele.baladr

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class PlaylistAdapter : ListAdapter<Track, TrackViewHolder>(ItemCallback()) {

    class ItemCallback : DiffUtil.ItemCallback<Track>() {
        override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean = (oldItem == newItem)
        override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean = (oldItem === newItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.view_playlist_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) = holder.bind(getItem(position))
}
