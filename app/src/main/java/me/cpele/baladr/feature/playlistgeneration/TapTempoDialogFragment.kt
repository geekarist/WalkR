package me.cpele.baladr.feature.playlistgeneration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.fragment_tap_tempo.*
import me.cpele.baladr.R

class TapTempoDialogFragment : DialogFragment() {

    companion object {
        fun newInstance(): TapTempoDialogFragment = TapTempoDialogFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_tap_tempo, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tapTempoClose.setOnClickListener { dismiss() }
    }
}
