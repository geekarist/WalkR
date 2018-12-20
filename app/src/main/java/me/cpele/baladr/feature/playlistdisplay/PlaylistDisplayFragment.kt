package me.cpele.baladr.feature.playlistdisplay

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_playlist_display.*
import me.cpele.baladr.CustomApp
import me.cpele.baladr.MainViewModel
import me.cpele.baladr.R
import me.cpele.baladr.common.business.TrackBo

class PlaylistDisplayFragment : Fragment() {

    private val viewModel: PlaylistDisplayViewModel by lazy {
        ViewModelProviders.of(
            this,
            CustomApp.instance.playlistDisplayViewModelFactory
        ).get(PlaylistDisplayViewModel::class.java)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        activity?.let {
            ViewModelProviders.of(it).get(MainViewModel::class.java)
        }?.postTitle(getString(R.string.display_label))
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

        viewModel.onPostTempo(arguments?.getInt("ARG_TEMPO"))
        viewModel.tracksData.observe(this, Observer { tracks: List<TrackBo> ->
            adapter.submitList(tracks)
        })

        viewModel.isButtonEnabled.observe(this, Observer {
            displaySaveButton.isEnabled = it
        })

        displaySaveButton.setOnClickListener {
            PlaylistNamingDialogFragment.newInstance().show(childFragmentManager, "TAG_CHOOSE_TEXT_DIALOG_FRAG")
        }

        viewModel.emptyViewVisibility.observe(this, Observer {
            displayEmpty.visibility = it
        })

        viewModel.recyclerViewVisibility.observe(this, Observer {
            displayList.visibility = it
        })

        viewModel.saveMsgEvent.observe(this, Observer {
            it.consumed?.let { msg ->
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()

                findNavController().navigate(R.id.action_playlistDisplayFragment_to_libraryFragment)
            }
        })

        viewModel.loginRequestEvent.observe(this, Observer {
            it.consumed?.let {
                AlertDialog.Builder(activity).setMessage("TODO: Login").show()
            }
        })
    }
}
