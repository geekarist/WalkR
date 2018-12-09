package me.cpele.baladr

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navHostFragment.findNavController().apply {
            addOnDestinationChangedListener { controller, destination, _ ->
                supportActionBar?.apply {
                    val isAtStart = destination.id == controller.graph.startDestination
                    setDisplayShowHomeEnabled(!isAtStart)
                    setDisplayHomeAsUpEnabled(!isAtStart)
                }
            }
        }

        val viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        viewModel.title.observe(this, Observer {
            supportActionBar?.title = it
        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> navHostFragment.findNavController().popBackStack()
            else -> super.onOptionsItemSelected(item)
        }
    }
}
