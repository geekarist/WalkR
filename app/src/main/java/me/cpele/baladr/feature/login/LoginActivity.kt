package me.cpele.baladr.feature.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView

class LoginActivity : Activity() {

    companion object {
        private const val ACTION_LAUNCH = "ACTION_LAUNCH"
        fun newIntent(ctx: Context): Intent = Intent(ctx, LoginActivity::class.java).setAction(ACTION_LAUNCH)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(TextView(this).apply { text = "Yo" })
    }
}
