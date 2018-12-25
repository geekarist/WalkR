package me.cpele.baladr.feature.login

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class LoginActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(TextView(this).apply { text = "Yo" })
    }
}
