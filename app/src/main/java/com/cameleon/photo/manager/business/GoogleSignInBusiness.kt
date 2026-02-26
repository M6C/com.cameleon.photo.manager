package com.cameleon.photo.manager.business

import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.cameleon.photo.manager.api.GoogleOAuthApi
import com.cameleon.photo.manager.api.GoogleUserApi
import com.cameleon.photo.manager.bean.dto.UserInfoResponse
import com.cameleon.photo.manager.di.module.ApiGoogleOAuth
import com.cameleon.photo.manager.repository.TokenRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import javax.inject.Inject

class GoogleSignInBusiness @Inject constructor() {

    companion object {
        private val TAG = GoogleSignInBusiness::class.simpleName
    }

    @Inject @ApiGoogleOAuth lateinit var googleOAuthApi: GoogleOAuthApi

    @Inject lateinit var googleUserApi: GoogleUserApi

    @Inject lateinit var tokenRepository: TokenRepository

    // Google Sign-In configuration
    fun singIn(
            activity: ComponentActivity,
            handleSignInResult: (GoogleSignInAccount) -> Unit
    ): ActivityResultLauncher<Intent> =
            activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                it
                        .run { data }
                        ?.let(GoogleSignIn::getSignedInAccountFromIntent)
                        // ?.run { try { result } catch (e : RuntimeException) { Log.e(TAG,
                        // "SignedIn Account Result Error:${e.message}", e); null} }
                        ?.runCatching { result }
                        ?.takeIf { it.isSuccess }
                        ?.run { this.getOrNull() }
                        ?.let { handleSignInResult.invoke(it) }
            }

    //    fun launchSingIn(activity: ComponentActivity) {
    //        val client = GoogleSignIn.getClient(activity, googleSignInOptions)
    //        signInLauncher.launch(client.signInIntent)
    //    }

    suspend fun handleSignInResult(
            account: GoogleSignInAccount,
            onSignIn: (UserInfoResponse?) -> Unit
    ) {
        val requiredScope = "https://www.googleapis.com/auth/photoslibrary"
        val hasScope = account.grantedScopes.any { it.scopeUri == requiredScope }
        Log.d(
                TAG,
                "HandleSignInResult: Granted Scopes: ${account.grantedScopes.joinToString { it.scopeUri }}"
        )
        Log.d(TAG, "HandleSignInResult: Has Required Scope ($requiredScope): $hasScope")

        if (!hasScope) {
            Log.e(TAG, "CRITICAL: Required scope NOT granted!")
            Log.e(TAG, "Scope $requiredScope missing from account")
            throw GoogleSignInException(GoogleSignInError.ACCESS_BLOCKED_API())
        }

        try {
            exchangeAuthCodeForTokens(account, onSignIn)
        } catch (e: RuntimeException) {
            when {
                e.message?.contains("com.google.android.gms.common.api.ApiException") ?: false -> {
                    val code =
                            e.message?.let { str ->
                                Regex("[0-9]+").findAll(str).lastOrNull()?.value?.toInt()
                            }
                                    ?: -1

                    val error = GoogleSignInError.findByCode(code)
                    Log.e(TAG, error.technicalMessage, e)
                    throw GoogleSignInException(error)
                }
                else -> {
                    Log.e(TAG, "Sign-in failed: ${e.message}", e)
                    throw e
                }
            }
        }
    }

    suspend fun getUserInfo(): UserInfoResponse {
        try {
            return googleUserApi.getUserInfo()
        } catch (e: Exception) {
            Log.e(TAG, "❌ UserInfo call failed: ${e.message}", e)
            throw GoogleSignInException(GoogleSignInError.USER_INFO_API_ERROR())
        }
    }

    suspend fun exchangeAuthCodeForTokens(
            account: GoogleSignInAccount,
            onSignIn: (UserInfoResponse?) -> Unit
    ) = exchangeAuthCodeForTokens(account.serverAuthCode, onSignIn)

    suspend fun exchangeAuthCodeForTokens(code: String?, onSignIn: (UserInfoResponse?) -> Unit) {
        val clientId = tokenRepository.getServerClientId()
        val clientSecret = tokenRepository.getClientSecret()
        Log.d(
                TAG,
                "ExchangeAuthCode: ClientID=${clientId.take(10)}... Secret=${clientSecret.take(5)}..."
        )
        try {
            if (code == null) return

            val response =
                    googleOAuthApi.getTokens(
                            clientId = clientId,
                            clientSecret = clientSecret,
                            code = code,
                            grantType = "authorization_code",
                            redirectUri = "",
                            scope = "https://www.googleapis.com/auth/photoslibrary"
                    )
            val accessToken = response.accessToken
            val refreshToken = response.refreshToken
            Log.d(TAG, "ExchangeAuthCode: Token Response Scope: ${response.scope}")
            Log.d(TAG, "ExchangeAuthCode: Access Token: ${accessToken?.take(10)}...")

            try {
                val tokenInfo = googleOAuthApi.getTokenInfo(accessToken ?: "").string()
                Log.d(TAG, "ExchangeAuthCode: Token Info: $tokenInfo")
            } catch (e: Exception) {
                Log.e(TAG, "ExchangeAuthCode: Failed to fetch token info", e)
            }

            tokenRepository.saveTokens(accessToken = accessToken, refreshToken = refreshToken)

            // ✅ Validation de l'auth : appel userinfo
            val userInfo = getUserInfo()
            Log.d(
                    TAG,
                    "✅ UserInfo OK: name=${userInfo.name}, email=${userInfo.email}, id=${userInfo.id}"
            )

            onSignIn(userInfo)
        } catch (e: Exception) {
            Log.e(
                    TAG,
                    "Google Exchange Auth For Token Api Call Failed '${e.message}\n${tokenRepository.showSecretsAndTokens()}",
                    e
            )
            if (e is GoogleSignInException) throw e
            throw RuntimeException("Google Exchange Auth For Token Api Call Failed: ${e.message}")
        }
    }
}
