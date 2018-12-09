package me.cpele.baladr.feature.playlist_display

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_playlist_display.*
import me.cpele.baladr.CustomApp
import me.cpele.baladr.MainViewModel
import me.cpele.baladr.R
import me.cpele.baladr.common.database.TrackBo

class PlaylistDisplayFragment : Fragment() {

    private val viewModel: PlaylistDisplayViewModel by lazy {
        ViewModelProviders.of(
            this,
            CustomApp.instance.playlistDisplayViewModelFactory
        ).get(PlaylistDisplayViewModel::class.java)
    }

    private val connectivityManager: ConnectivityManager? by lazy {
        context?.let {
            ContextCompat.getSystemService(it, ConnectivityManager::class.java)
        }
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

    private val networkCallback: ConnectivityManager.NetworkCallback by lazy {
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network?) {
                super.onAvailable(network)
                viewModel.onConnectivityChange(true)
            }

            override fun onLost(network: Network?) {
                super.onLost(network)
                viewModel.onConnectivityChange(false)
            }
        }
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

        val req = NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build()
        connectivityManager?.registerNetworkCallback(req, networkCallback)

        displaySaveButton.setOnClickListener {
            viewModel.onClickSave()
        }

        viewModel.emptyViewVisibility.observe(this, Observer {
            displayEmpty.visibility = it
        })

        viewModel.recyclerViewVisibility.observe(this, Observer {
            displayList.visibility = it
        })

        viewModel.playlistSaveEvent.observe(this, Observer {
            it.consumed?.apply {
                Toast.makeText(
                    context,
                    "Playlist '$name' has been saved",
                    Toast.LENGTH_LONG
                ).show()

                findNavController().navigate(R.id.action_playlistDisplayFragment_to_libraryFragment)
            }
        })
    }

    override fun onDestroyView() {
        connectivityManager?.unregisterNetworkCallback(networkCallback)
        super.onDestroyView()
    }
}
