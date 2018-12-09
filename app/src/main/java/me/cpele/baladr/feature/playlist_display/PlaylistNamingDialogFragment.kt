package me.cpele.baladr.feature.playlist_display

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import me.cpele.baladr.R

class PlaylistNamingDialogFragment : BottomSheetDialogFragment() {
    companion object {
        fun newInstance() = PlaylistNamingDialogFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_playlist_naming, container, false)
    }
}
