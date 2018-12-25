package me.cpele.baladr

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.activity_main.*
import me.cpele.baladr.feature.login.LoginActivity

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        android.R.id.home -> navHostFragment.findNavController().popBackStack()
        R.id.menuMainLogin -> {
            startActivity(Intent(this, LoginActivity::class.java))
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
