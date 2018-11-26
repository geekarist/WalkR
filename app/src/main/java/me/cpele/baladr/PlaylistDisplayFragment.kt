package me.cpele.baladr

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_playlist_display.*

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
                viewModel.onNetworkActive(true)
            }

            override fun onLost(network: Network?) {
                super.onLost(network)
                viewModel.onNetworkActive(false)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = PlaylistAdapter()
        displayList.adapter = adapter

        viewModel.onPostTempo(arguments?.getInt("ARG_TEMPO"))
        viewModel.tracks.observe(this, Observer { tracks: List<TrackBo> ->
            adapter.submitList(tracks)
        })

        viewModel.isButtonEnabled.observe(this, Observer {
            displayButton.isEnabled = it
        })

        val req = NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build()
        connectivityManager?.registerNetworkCallback(req, networkCallback)
    }

    override fun onDestroyView() {
        connectivityManager?.unregisterNetworkCallback(networkCallback)
        super.onDestroyView()
    }
}
