package me.cpele.baladr.feature.library

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.lopei.collageview.CollageView
import kotlinx.android.synthetic.main.view_library_playlist.view.*
import me.cpele.baladr.common.database.PlaylistWithTracksEntity

class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(item: PlaylistWithTracksEntity?) {
        item?.apply {
            itemView.playlistItemName.text = playlist.name
            itemView.playlistItemTrackCount.text = tracks?.size.toString()
            itemView.playlistItemCover.useFirstAsHeader(false)
                .defaultPhotosForLine(2)
                .useFirstAsHeader(false)
                .useCards(false)
                .photosForm(CollageView.ImageForm.IMAGE_FORM_SQUARE)
                .loadPhotos(tracks?.map { it.cover })
        }
    }
}
