package me.cpele.baladr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_playlist_display.*

class PlaylistDisplayFragment : Fragment() {

    private val viewModel: PlaylistDisplayViewModel by lazy {
        ViewModelProviders.of(this).get(PlaylistDisplayViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_playlist_display, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = PlaylistAdapter()
        displayList.adapter = adapter
        viewModel.tracks.observe(this, Observer { tracks: List<Track> ->
            adapter.submitList(tracks)
        })
    }
}
