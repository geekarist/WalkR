package me.cpele.baladr.feature.playlistgeneration


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_playlist_generation.*
import me.cpele.baladr.CustomApp
import me.cpele.baladr.MainViewModel
import me.cpele.baladr.R


class PlaylistGenerationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_playlist_generation, container, false)
    }

    private val viewModel: PlaylistGenerationViewModel by lazy {
        ViewModelProviders.of(
            this,
            CustomApp.instance.playlistGenerationViewModelFactory
        ).get(PlaylistGenerationViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        generationTempoSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.onProgressChanged(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })

        viewModel.progress.observe(this, Observer { progress: Int ->
            generationTempoSeekBar.progress = progress
        })

        viewModel.tempo.observe(this, Observer {
            generationTempoMaxLabel.text = it.toString()
        })

        generationButton.setOnClickListener {
            val bundle = bundleOf("ARG_TEMPO" to viewModel.tempo.value)
            findNavController().navigate(
                R.id.action_playlistGenerationFragment_to_playlistDisplayFragment,
                bundle
            )
        }

        viewModel.tempoDetectButtonEnabled.observe(this, Observer { isEnabled: Boolean ->
            generationTempoDetectButton.isEnabled = isEnabled
        })

        viewModel.seekBarEnabled.observe(this, Observer {
            generationTempoSeekBar.isEnabled = it
        })

        viewModel.viewEventData.observe(this, Observer {
            it.consumed?.let { viewEvent -> renderEvent(viewEvent) }
        })

        generationTempoDetectButton.setOnClickListener {
            viewModel.onStartTempoDetection(durationSeconds = 10)
        }

        generationTempoTapButton.setOnClickListener {
            TapTempoDialogFragment.newInstance().show(childFragmentManager, TAG_FRAGMENT_TAP_TEMPO)
        }
    }

    private fun renderEvent(viewEvent: PlaylistGenerationViewModel.ViewEvent) =
        when (viewEvent) {
            is PlaylistGenerationViewModel.ViewEvent.Toast -> Toast.makeText(
                context,
                getString(viewEvent.message, viewEvent.cause),
                Toast.LENGTH_SHORT
            ).show()
        }

    override fun onResume() {
        super.onResume()
        activity?.let {
            ViewModelProviders.of(it).get(MainViewModel::class.java)
        }?.postTitle(getString(R.string.generation_title))
    }

    companion object {
        private const val TAG_FRAGMENT_TAP_TEMPO = "TAG_FRAGMENT_TAP_TEMPO"
        private const val TAG_FRAGMENT_CALIBRATION = "TAG_FRAGMENT_CALIBRATION"
    }
}
