package me.cpele.baladr

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.view_playlist_track.view.*

class TrackViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(item: TrackBo?) {
        itemView.trackTitle.text = item?.title
        itemView.trackArtist.text = item?.artist
        itemView.trackDuration.text = item?.duration
        // TODO itemView.trackCover
    }

}
