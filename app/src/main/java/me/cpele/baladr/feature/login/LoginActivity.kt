package me.cpele.baladr.feature.login

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import kotlinx.coroutines.*
import me.cpele.baladr.BuildConfig
import me.cpele.baladr.CustomApp
import me.cpele.baladr.R
import net.openid.appauth.*
import kotlin.coroutines.CoroutineContext

class LoginActivity : Activity(), CoroutineScope {

    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    companion object {
        private const val ACTION_LAUNCH = "ACTION_LAUNCH"
        private const val ACTION_HANDLE_CODE = "ACTION_HANDLE_CODE"
        private const val ACTION_CANCEL_AUTH = "ACTION_CANCEL_AUTH"
        fun newIntent(ctx: Context): Intent = Intent(ctx, LoginActivity::class.java).setAction(ACTION_LAUNCH)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        job = Job()

        when (intent?.action) {
            ACTION_LAUNCH -> requestCode()
            ACTION_HANDLE_CODE -> requestToken()
            ACTION_CANCEL_AUTH -> {
                Toast.makeText(this, getString(R.string.login_auth_canceled), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private val authService by lazy {
        AuthorizationService(this)
    }

    private fun requestCode() {
        val config = AuthorizationServiceConfiguration(
            Uri.parse("https://accounts.spotify.com/authorize"),
            Uri.parse("https://accounts.spotify.com/api/token")
        )
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
        val canceledPendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, javaClass).setAction(ACTION_CANCEL_AUTH).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP),
            0
        )
        launch(Dispatchers.IO) {
            authService.performAuthorizationRequest(authRequest, completedPendingIntent, canceledPendingIntent)
        }
    }

    private fun requestToken() {
        val authResponse = AuthorizationResponse.fromIntent(intent)
        val authError = AuthorizationException.fromIntent(intent)
        val authState = AuthState(authResponse, authError)
        authResponse?.createTokenExchangeRequest()?.let { tokenRequest ->
            authService.performTokenRequest(
                tokenRequest,
                ClientSecretBasic(BuildConfig.SPOTIFY_CLIENT_SECRET)
            ) { response, ex ->
                authState.update(response, ex)
                launch(Dispatchers.IO) {
                    CustomApp.instance.authStateRepository.set(authState)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity, getString(R.string.login_successful), Toast.LENGTH_SHORT)
                            .show()
                        finish()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
}
