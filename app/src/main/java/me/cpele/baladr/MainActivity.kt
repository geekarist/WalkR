package me.cpele.baladr

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navHostFragment.findNavController().apply {
            addOnNavigatedListener { controller, destination ->
                supportActionBar?.apply {
                    val isAtStart = destination.id == controller.graph.startDestination
                    setDisplayShowHomeEnabled(!isAtStart)
                    setDisplayHomeAsUpEnabled(!isAtStart)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> navHostFragment.findNavController().popBackStack()
            else -> super.onOptionsItemSelected(item)
        }
    }
}
