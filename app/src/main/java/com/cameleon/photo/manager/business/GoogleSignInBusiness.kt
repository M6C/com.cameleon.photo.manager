package com.cameleon.photo.manager.business

import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.cameleon.photo.manager.api.GoogleOAuthApi
import com.cameleon.photo.manager.di.module.ApiGoogleOAuth
import com.cameleon.photo.manager.repository.TokenRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import javax.inject.Inject

class GoogleSignInBusiness @Inject constructor() {

    companion object {
        private val TAG = GoogleSignInBusiness::class.simpleName
    }

    @Inject
    @ApiGoogleOAuth
    lateinit var googleOAuthApi: GoogleOAuthApi

    @Inject
    lateinit var tokenRepository: TokenRepository


    // Google Sign-In configuration
    fun singIn(activity: ComponentActivity, handleSignInResult: (GoogleSignInAccount) -> Unit): ActivityResultLauncher<Intent> =
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it
                .run { data }
                ?.let ( GoogleSignIn::getSignedInAccountFromIntent )
//                ?.run { try { result } catch (e : RuntimeException) { Log.e(TAG, "SignedIn Account Result Error:${e.message}", e); null} }
                ?.runCatching { result }?.takeIf { it.isSuccess }?.run { this.getOrNull() }
                ?.let { handleSignInResult.invoke(it) }
        }

//    fun launchSingIn(activity: ComponentActivity) {
//        val client = GoogleSignIn.getClient(activity, googleSignInOptions)
//        signInLauncher.launch(client.signInIntent)
//    }

    suspend fun handleSignInResult(account: GoogleSignInAccount, onSignIn: () -> Unit) {
        try {
            exchangeAuthCodeForTokens(account, onSignIn)
        } catch (e: RuntimeException) {
            when {
                e.message?.contains("com.google.android.gms.common.api.ApiException") ?: false -> {

                    val code = e.message?.let { str ->
                        Regex("[0-9]+").findAll(str).lastOrNull()?.value?.toInt()
                    } ?: -1

                    throw
                        GoogleSignInError.findByCode(code)
                            .also { Log.e(TAG, it.technicalMessage, e) }
                            .let(::GoogleSignInException)
                }
                else -> {
                    Log.e(TAG, "Sign-in failed: ${e.message}", e)
                    throw e
                }
            }
        }
    }

    suspend fun exchangeAuthCodeForTokens(account: GoogleSignInAccount, onSignIn: () -> Unit) =
        exchangeAuthCodeForTokens(account.serverAuthCode, onSignIn)

    suspend fun exchangeAuthCodeForTokens(code: String?, onSignIn: () -> Unit) {
        val clientId = tokenRepository.getServerClientId()
        val clientSecret = tokenRepository.getClientSecret()
        try {
            if (code == null)
                return

            val response = googleOAuthApi.getTokens(
                clientId = clientId,
                clientSecret = clientSecret,
                code = code,
                grantType = "authorization_code",
                redirectUri = ""
            )
            val accessToken = response.accessToken
            val refreshToken = response.refreshToken
            tokenRepository.saveTokens(
                accessToken = accessToken,
                refreshToken = refreshToken
            )

            onSignIn()
        } catch (e: RuntimeException) {
            Log.e(TAG, "Google Exchange Auth For Token Api Call Failed '${e.message}\n${tokenRepository.showSecretsAndTokens()}", e)
        }
    }
}