package me.cpele.baladr.feature.playlistgeneration


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_playlist_generation.*
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
        ViewModelProviders.of(this).get(PlaylistGenerationViewModel::class.java)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        activity?.let {
            ViewModelProviders.of(it).get(MainViewModel::class.java)
        }?.postTitle(getString(R.string.generation_title))
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

        generationTempoDetectButton.setOnClickListener {
            viewModel.onStartTempoDetection(durationSeconds = 10)
        }
    }
}
