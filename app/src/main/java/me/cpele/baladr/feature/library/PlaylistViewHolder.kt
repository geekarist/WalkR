package me.cpele.baladr.feature.library

import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.lopei.collageview.CollageView
import kotlinx.android.synthetic.main.view_library_playlist.view.*
import me.cpele.baladr.R
import me.cpele.baladr.common.business.PlaylistBo

class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(item: PlaylistBo?) {
        item?.apply {
            itemView.playlistItemName.text = name
            itemView.playlistItemTrackCount.text = itemView.context.getString(R.string.library_count, tracks.size)
            itemView.playlistItemCover.useFirstAsHeader(false)
                .defaultPhotosForLine(2)
                .useFirstAsHeader(false)
                .useCards(false)
                .photosForm(CollageView.ImageForm.IMAGE_FORM_SQUARE)
                .loadPhotos(tracks.takeLast(4).map { it.cover })
            itemView.playlistItemDate.text = date.toString()
            itemView.setOnClickListener {
                if (uri == null) {
                    Toast.makeText(itemView.context, "Playlist has no URI", Toast.LENGTH_SHORT).show()
                } else {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                    if (intent.resolveActivity(itemView.context.packageManager) != null) {
                        itemView.context.startActivity(intent)
                    } else {
                        Toast.makeText(
                            itemView.context,
                            "No application found to open this playlist",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}
