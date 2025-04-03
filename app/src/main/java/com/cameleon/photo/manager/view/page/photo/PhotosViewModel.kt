package com.cameleon.photo.manager.view.page.photo

import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cameleon.photo.manager.R
import com.cameleon.photo.manager.business.GoogleAuthBusiness
import com.cameleon.photo.manager.business.GoogleSignInBusiness
import com.cameleon.photo.manager.business.GoogleSignInError
import com.cameleon.photo.manager.business.GoogleSignInError.INTERNET_CONNECTION_ERROR
import com.cameleon.photo.manager.business.GoogleSignInException
import com.cameleon.photo.manager.business.TokenBusiness
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject

@HiltViewModel
class PhotosViewModel @Inject constructor(private val googleAuthBusiness: GoogleAuthBusiness,  private val googleSignInBusiness: GoogleSignInBusiness, private val googleSignInClient: GoogleSignInClient, private val googleSignInOptions : GoogleSignInOptions, private val tokenBusiness: TokenBusiness) : ViewModel() {

    companion object {
        val TAG = PhotosViewModel::class.simpleName
    }

    private val _isSignedIn = MutableStateFlow(false)
    val isSignedIn: StateFlow<Boolean> = _isSignedIn

    private val _photos = MutableStateFlow<List<String>>(emptyList())
    val photos: StateFlow<List<String>> = _photos

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _onShowUserMessage = MutableStateFlow<String?>(null)
    private val onShowUserMessage: StateFlow<String?> = _onShowUserMessage
    @Composable
    fun getUserMessage() = _onShowUserMessage.collectAsState().value.also { Log.i(TAG, "-----> getUserMessage value:$it"); if (it != null) { _onShowUserMessage.value = null } }

    private val _onShowUserError = MutableStateFlow<String?>(null)
    private val onShowUserError: StateFlow<String?> = _onShowUserError
    @Composable
    fun getUserError() = _onShowUserError.collectAsState().value.also { Log.i(TAG, "-----> getUserError value:$it"); if (it != null) { _onShowUserError.value = null } }

    private var authToken: String? = null

    private var signInLauncher: ActivityResultLauncher<Intent>? = null

    fun singIn(activity: ComponentActivity, onSignIn: () -> Unit ) {
        signInLauncher =
            googleSignInBusiness.singIn(activity) {
                handleSignInResult(it, activity, onSignIn)
            }
    }

    fun launchSingIn(activity: ComponentActivity) {
        val client = GoogleSignIn.getClient(activity, googleSignInOptions)
        signInLauncher?.launch(client.signInIntent)
    }

    fun logOut() {
        googleSignInClient.signOut().addOnCompleteListener {
            tokenBusiness.clearTokens()
            authToken = null
            _isSignedIn.value = false
        }
    }

    private fun handleSignInResult(account: GoogleSignInAccount, activity: ComponentActivity, onSignIn: () -> Unit) {
        Log.i(TAG, "-----> handleSignInResult")
        authToken = tokenBusiness.getAccessToken()
        _isSignedIn.value = !authToken.isNullOrEmpty()

//        exchangeAuthCodeForTokens(account, activity.getString(R.string.server_client_id), activity.getString(R.string.client_secret), onSignIn)
        viewModelScope.launch {
            try {
                googleSignInBusiness.handleSignInResult(account, activity.getString(R.string.server_client_id), activity.getString(R.string.client_secret)) {
                    Log.i(TAG, "-----> handleSignInResult Successful")
                    viewModelScope.launch {
                        _onShowUserMessage.emit("Login Successful")
                    }
                    fetchPhotos()
                }
            } catch (e: GoogleSignInException) {
                Log.i(TAG, "-----> handleSignInResult GoogleSignInException")
                Log.e(TAG, e.message, e)
                _onShowUserError.value =
                    (
                    when (e.error) {
                        is INTERNET_CONNECTION_ERROR -> "Sign-in failed - ApiException - Internet Connection Error"
                        is GoogleSignInError.OAUTH2_CERTIFICATE_ERROR -> "Sign-in failed - ApiException - SHA-1 of signing certificate Required in Google Cloud Console. Create an OAuth2 client and API key for your app"
                        is GoogleSignInError.ACCESS_ERROR_API -> "Sign-in failed - ApiException - Access/Authorization Error API"
                        is GoogleSignInError.ACCESS_BLOCKED_API -> "Sign-in failed - ApiException - Access Blocked API"
                        is GoogleSignInError.AUTHENTICATION_ALREADY_CALL -> "Sign-in failed - ApiException - An Other API Authentication Already Running"
                        is GoogleSignInError.UNKOWN_ERROR -> "Sign-in failed - ApiException - Unknown Code:${e.error.code}"
                    } + " : ${e.message}"
                    ).also { Log.i(TAG, "-----> handleSignInResult GoogleSignInException error:$it") }
            }
        }
    }

//    private fun exchangeAuthCodeForTokens(account: GoogleSignInAccount, clientId: String, clientSecret: String, onSignIn: () -> Unit) {
//        viewModelScope.launch {
//            try {
//                googleAuthBusiness.exchangeAuthCodeForTokens(account, clientId, clientSecret, onSignIn)
//            } catch (e: RuntimeException) {
//                Log.e(TAG, "Google Exchange Auth For Token Api Call Failed '${e.message}\nWith account:$account clientId:$clientId clientSecret:$clientSecret Auth Token:$authToken", e)
//            }
//        }
//    }

    private fun fetchPhotos() {
        Log.i(TAG, "-----> fetchPhotos with authToken:$authToken")
        if (authToken.isNullOrEmpty()) return

        _isLoading.value = true

        viewModelScope.launch {
            try {
                val photos1 = googleAuthBusiness.createGooglePhotosApi(authToken!!).getPhotos()
                val mediaItems = photos1.mediaItems
                val photos = mediaItems.map { it.baseUrl }
                _photos.value = photos
                Log.i(TAG, "-----> fetchPhotos size:${photos.size}")
                this@PhotosViewModel._isSignedIn.value = true
            } catch (e: Exception) {
                Log.i(TAG, "-----> fetchPhotos Exception:${e.message}")
                Log.e(TAG, "Google Photos Api Call for retreive Photos Failed '${e.message}\nWith Auth Token: $authToken", e)
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        this@PhotosViewModel._isSignedIn.value = false
                    }
                }, 5_000)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
