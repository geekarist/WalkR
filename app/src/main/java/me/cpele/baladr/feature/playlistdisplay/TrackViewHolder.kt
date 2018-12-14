package me.cpele.baladr.feature.playlistdisplay

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.view_playlist_track.view.*
import me.cpele.baladr.common.business.TrackBo

class TrackViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(item: TrackBo?) {
        itemView.trackTitle.text = item?.title
        itemView.trackArtist.text = item?.artist
        itemView.trackDuration.text = item?.duration
        item?.apply {
            Glide.with(itemView).load(cover).into(itemView.trackCover)
        }
    }

}
