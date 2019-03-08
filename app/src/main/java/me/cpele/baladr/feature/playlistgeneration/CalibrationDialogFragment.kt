package me.cpele.baladr.feature.playlistgeneration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_calibration.view.*
import me.cpele.baladr.CustomApp
import me.cpele.baladr.R

class CalibrationDialogFragment : DialogFragment() {

    companion object {
        fun newInstance() = CalibrationDialogFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_calibration, container, false)
    }

    private val viewModel: CalibrationViewModel by lazy {
        ViewModelProviders.of(
            this,
            CustomApp.instance.calibrationViewModelFactory
        ).get(CalibrationViewModel::class.java)
    }

    private val parentViewModel: PlaylistGenerationViewModel? by lazy {
        parentFragment?.let {
            ViewModelProviders.of(
                it,
                CustomApp.instance.mainViewModelFactory
            ).get(PlaylistGenerationViewModel::class.java)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.calibrationClose.setOnClickListener { dismiss() }
        view.calibrationTapButton.setOnClickListener { viewModel.onTap() }
        view.calibrationResetButton.setOnClickListener { viewModel.onReset() }

        viewModel.detectedTempoStr.observe(this, Observer {
            view.calibrationDetectedCount.text = it
        })

        viewModel.tapTempoStr.observe(this, Observer {
            view.calibrationTapCount.text = it?.toString()
        })

        viewModel.tapTempo.observe(this, Observer {
            it?.let {
                parentViewModel?.onTempoChangedExternally(it)
            }
        })
    }
}