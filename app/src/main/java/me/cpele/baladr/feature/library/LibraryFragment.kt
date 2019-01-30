package me.cpele.baladr.feature.library

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_library.*
import me.cpele.baladr.CustomApp
import me.cpele.baladr.MainViewModel
import me.cpele.baladr.R

class LibraryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    private val viewModel: LibraryViewModel by lazy {
        ViewModelProviders.of(this, CustomApp.instance.libraryViewModelFactory).get(LibraryViewModel::class.java)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        activity?.let {
            ViewModelProviders.of(it).get(MainViewModel::class.java)
        }?.postTitle(getString(R.string.library_title))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        libraryButton.setOnClickListener {
            findNavController().navigate(R.id.action_libraryFragment_to_playlistGenerationFragment)
        }

        val libAdapter = LibraryAdapter()
        libraryList.apply {
            adapter = libAdapter
            layoutManager = LinearLayoutManager(context)
        }

        viewModel.libItems.observe(this, Observer { items ->
            libAdapter.submitList(items)
        })
    }
}
