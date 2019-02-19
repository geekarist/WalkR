package me.cpele.baladr.feature.playlistgeneration

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import me.cpele.baladr.R

class FitLoginActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_fit_login)

        val options = GoogleSignInOptions.Builder()
            .requestEmail()
            .requestScopes(Scope(Scopes.FITNESS_ACTIVITY_READ))
            .build()
        val client = GoogleSignIn.getClient(this, options)
        val intent = client.signInIntent
        startActivityForResult(intent, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0) {
            val signedInAccount = GoogleSignIn.getSignedInAccountFromIntent(data)
            if (signedInAccount.isComplete) {
                finish()
            }
        }
    }
}
