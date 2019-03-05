package me.cpele.baladr.feature.playlistgeneration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_tap_tempo.view.*
import me.cpele.baladr.CustomApp
import me.cpele.baladr.R

class CalibrationDialogFragment : DialogFragment() {

    companion object {
        fun newInstance() = CalibrationDialogFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tap_tempo, container, false)
    }

    private val viewModel: CalibrationViewModel by lazy {
        ViewModelProviders.of(
            this,
            CustomApp.instance.calibrationViewModelFactory
        ).get(CalibrationViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.tapTempoClose.setOnClickListener { dismiss() }
        view.tapTempoButton.setOnClickListener { viewModel.onTap() }
        view.tapTempoReset.setOnClickListener { viewModel.onReset() }
    }
}