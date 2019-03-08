package me.cpele.baladr.feature.playlistgeneration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_tap_tempo.*
import me.cpele.baladr.CustomApp
import me.cpele.baladr.R

class TapTempoDialogFragment : DialogFragment() {

    companion object {
        fun newInstance(): TapTempoDialogFragment = TapTempoDialogFragment()
    }

    private val viewModel: TapTempoViewModel by lazy {
        ViewModelProviders.of(
            this,
            CustomApp.instance.tapTempoViewModelFactory
        ).get(TapTempoViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_tap_tempo, container, false)

    private val parentViewModel: PlaylistGenerationViewModel? by lazy {
        parentFragment?.let {
            ViewModelProviders.of(it).get(PlaylistGenerationViewModel::class.java)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tapTempoClose.setOnClickListener { dismiss() }
        tapTempoReset.setOnClickListener { viewModel.onReset() }
        tapTempoButton.setOnClickListener { viewModel.onTap() }

        viewModel.beatsPerMinStr.observe(this, Observer<String> {
            tapTempoCount.text = it
        })

        viewModel.beatsPerMin.observe(this, Observer {
            it?.let { tempo ->
                parentViewModel?.onTempoChangedExternally(tempo)
            }
        })
    }
}
