package me.cpele.baladr.feature.playlistdisplay

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import me.cpele.baladr.R
import me.cpele.baladr.common.database.TrackEntity

class PlaylistAdapter : ListAdapter<TrackEntity, TrackViewHolder>(ItemCallback()) {

    class ItemCallback : DiffUtil.ItemCallback<TrackEntity>() {
        override fun areItemsTheSame(oldItem: TrackEntity, newItem: TrackEntity) = (oldItem === newItem)
        override fun areContentsTheSame(oldItem: TrackEntity, newItem: TrackEntity) = (oldItem == newItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.view_playlist_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) = holder.bind(getItem(position))
}
