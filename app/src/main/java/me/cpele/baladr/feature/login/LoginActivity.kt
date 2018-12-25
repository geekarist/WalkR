package me.cpele.baladr.feature.login

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues

class LoginActivity : Activity() {

    companion object {
        private const val ACTION_LAUNCH = "ACTION_LAUNCH"
        private const val ACTION_HANDLE_CODE = "ACTION_HANDLE_CODE"
        fun newIntent(ctx: Context): Intent = Intent(ctx, LoginActivity::class.java).setAction(ACTION_LAUNCH)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when (intent?.action) {
            ACTION_LAUNCH -> requestAuthCode()
            ACTION_HANDLE_CODE -> Toast.makeText(this, "Yo", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestAuthCode() {
        val config = AuthorizationServiceConfiguration(
            Uri.parse("https://accounts.spotify.com/authorize"),
            Uri.parse("https://accounts.spotify.com/api/token")
        )
        val authService = AuthorizationService(this)
        val authRequest = AuthorizationRequest.Builder(
            config,
            "e4927569efc54bc08036701294ca33db",
            ResponseTypeValues.CODE,
            Uri.parse("baladr://handle-response")
        ).setScopes("playlist-modify-private", "playlist-modify-public").build()

        val completedPendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, javaClass).setAction(ACTION_HANDLE_CODE).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP),
            0
        )
        authService.performAuthorizationRequest(authRequest, completedPendingIntent)
    }
}
