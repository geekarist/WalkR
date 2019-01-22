package me.cpele.baladr

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

    private val viewModel by lazy {
        ViewModelProviders.of(
            this,
            CustomApp.instance.mainViewModelFactory
        ).get(MainViewModel::class.java)
    }

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

        viewModel.title.observe(this, Observer {
            supportActionBar?.title = it
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val loginItem = menu?.findItem(R.id.menuMainLogin)
        val logoutItem = menu?.findItem(R.id.menuMainLogout)

        viewModel.isLoginVisible.observe(this, Observer { loginItem?.isVisible = it })
        viewModel.isLogoutVisible.observe(this, Observer { logoutItem?.isVisible = it })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        android.R.id.home -> navHostFragment.findNavController().popBackStack()
        R.id.menuMainLogin -> {
            startActivity(LoginActivity.newIntent(this))
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
