package me.cpele.baladr.feature.playlistdisplay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_playlist_naming.*
import me.cpele.baladr.R

class PlaylistNamingDialogFragment : BottomSheetDialogFragment() {
    companion object {
        fun newInstance() = PlaylistNamingDialogFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_playlist_naming, container, false)
    }

    private val parentViewModel by lazy {
        parentFragment?.let { ViewModelProviders.of(it) }
            ?.get(PlaylistDisplayViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistNamingConfirmButton.setOnClickListener {
            parentViewModel?.onConfirmSave(playlistNamingEdit.text.toString())
        }

        playlistNamingCancelButton.setOnClickListener { dismiss() }
    }
}
