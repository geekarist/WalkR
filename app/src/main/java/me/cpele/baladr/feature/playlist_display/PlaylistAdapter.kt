package me.cpele.baladr.feature.playlist_display

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import me.cpele.baladr.R
import me.cpele.baladr.common.database.TrackBo

class PlaylistAdapter : ListAdapter<TrackBo, TrackViewHolder>(ItemCallback()) {

    class ItemCallback : DiffUtil.ItemCallback<TrackBo>() {
        override fun areItemsTheSame(oldItem: TrackBo, newItem: TrackBo): Boolean = (oldItem == newItem)
        override fun areContentsTheSame(oldItem: TrackBo, newItem: TrackBo): Boolean = (oldItem === newItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.view_playlist_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) = holder.bind(getItem(position))
}
