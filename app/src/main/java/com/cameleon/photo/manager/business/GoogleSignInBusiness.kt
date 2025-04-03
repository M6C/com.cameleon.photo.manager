package com.cameleon.photo.manager.business

import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.cameleon.photo.manager.api.GoogleOAuthApi
import com.cameleon.photo.manager.ui.activity.MainActivity.Companion.TAG
import com.cameleon.photo.manager.view.page.photo.PhotosViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import retrofit2.Retrofit
import javax.inject.Inject

class GoogleSignInBusiness @Inject constructor(private val clientHttp: Retrofit, private val googleSignInOptions : GoogleSignInOptions, private val tokenBusiness: TokenBusiness) {

    // Google Sign-In configuration
    fun singIn(activity: ComponentActivity, handleSignInResult: (GoogleSignInAccount) -> Unit): ActivityResultLauncher<Intent> =
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it
                .run { data }
                ?.let ( GoogleSignIn::getSignedInAccountFromIntent )
                ?.run { result }
                ?.let { handleSignInResult.invoke(it) }
        }

//    fun launchSingIn(activity: ComponentActivity) {
//        val client = GoogleSignIn.getClient(activity, googleSignInOptions)
//        signInLauncher.launch(client.signInIntent)
//    }

    suspend fun handleSignInResult(account: GoogleSignInAccount, serverClientId: String, clientSecret : String, onSignIn: () -> Unit) {
        try {
            exchangeAuthCodeForTokens(account, serverClientId, clientSecret, onSignIn)
        } catch (e: RuntimeException) {
            when {
                e.message?.contains("com.google.android.gms.common.api.ApiException") ?: false -> {

                    val code = e.message?.let { str ->
                        Regex("[0-9]+").findAll(str).lastOrNull()?.value?.toInt()
                    } ?: -1

//                    val error = when (code) {
//                        7 -> GoogleSignInError.INTERNET_CONNECTION_ERROR()
//                        10 -> GoogleSignInError.OAUTH2_CERTIFICATE_ERROR()
//                        12500 -> GoogleSignInError.ACCESS_ERROR_API()
//                        12501 -> GoogleSignInError.ACCESS_BLOCKED_API()
//                        12502 ->  GoogleSignInError.AUTHENTICATION_ALREADY_CALL()
//                        else -> GoogleSignInError.UNKOWN_ERROR(code, e)
//                    }
                    throw
                        GoogleSignInError.findByCode(code)
                            .also { Log.e(TAG, it.technicalMessage, e) }
                            .let(::GoogleSignInException)
                }
                else -> {
                    Log.e(TAG, "Sign-in failed: ${e.message}", e)
//                    Toast.makeText(this, "Sign-in failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    suspend fun exchangeAuthCodeForTokens(account: GoogleSignInAccount, clientId: String, clientSecret: String, onSignIn: () -> Unit) {
        try {
            val authCode = account.serverAuthCode
            if (authCode == null)
                return

            val api = clientHttp.create(GoogleOAuthApi::class.java)

            val response = api.getTokens(
                clientId = clientId,
                clientSecret = clientSecret,
                code = authCode,
                grantType = "authorization_code",
                redirectUri = ""
            )
            val accessToken = response.accessToken
            val refreshToken = response.refreshToken
            tokenBusiness.saveTokens(
                accessToken = accessToken,
                refreshToken = refreshToken
            )

            onSignIn()
        } catch (e: RuntimeException) {
            val accessToken = tokenBusiness.getAccessToken()
            Log.e(PhotosViewModel.TAG, "Google Exchange Auth For Token Api Call Failed '${e.message}\nWith account:$account clientId:$clientId clientSecret:$clientSecret Auth Token:$accessToken", e)
        }
    }
}