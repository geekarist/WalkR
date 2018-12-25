package me.cpele.baladr.feature.login

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import kotlinx.coroutines.*
import me.cpele.baladr.BuildConfig
import me.cpele.baladr.CustomApp
import net.openid.appauth.*
import kotlin.coroutines.CoroutineContext

class LoginActivity : Activity(), CoroutineScope {

    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    companion object {
        private const val ACTION_LAUNCH = "ACTION_LAUNCH"
        private const val ACTION_HANDLE_CODE = "ACTION_HANDLE_CODE"
        fun newIntent(ctx: Context): Intent = Intent(ctx, LoginActivity::class.java).setAction(ACTION_LAUNCH)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        job = Job()

        when (intent?.action) {
            ACTION_LAUNCH -> requestCode()
            ACTION_HANDLE_CODE -> requestToken()
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
        launch(Dispatchers.IO) {
            authService.performAuthorizationRequest(authRequest, completedPendingIntent)
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
                authState.performActionWithFreshTokens(authService) { accessToken, _, _ ->
                    accessToken?.let {
                        launch(Dispatchers.IO) {
                            CustomApp.instance.database.accessTokenDao().set(it)
                            withContext(Dispatchers.Main) {
                                finish()
                            }
                        }
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
