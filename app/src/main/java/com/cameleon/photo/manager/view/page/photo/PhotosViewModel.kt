package com.cameleon.photo.manager.view.page.photo

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cameleon.photo.manager.business.GoogleAuthBusiness
import com.cameleon.photo.manager.business.TokenBusiness
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject

@HiltViewModel
class PhotosViewModel @Inject constructor(private val googleAuthBusiness: GoogleAuthBusiness, private val googleSignInClient: GoogleSignInClient, private val tokenBusiness: TokenBusiness) : ViewModel() {

    companion object {
        val TAG = PhotosViewModel::class.simpleName
    }


    private val _isSignedIn = MutableStateFlow(false)
    val isSignedIn: StateFlow<Boolean> = _isSignedIn

    private val _photos = MutableStateFlow<List<String>>(emptyList())
    val photos: StateFlow<List<String>> = _photos

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var authToken: String? = null

    fun signIn() {
        authToken = tokenBusiness.getAccessToken()
        _isSignedIn.value = !authToken.isNullOrEmpty()
        fetchPhotos()
    }

    fun logOut() {
        googleSignInClient.signOut().addOnCompleteListener {
            tokenBusiness.clearTokens()
            authToken = null
            _isSignedIn.value = false
        }
    }

    fun exchangeAuthCodeForTokens(account: GoogleSignInAccount, clientId: String, clientSecret: String, onSignIn: () -> Unit) {
        viewModelScope.launch {
            try {
                googleAuthBusiness.exchangeAuthCodeForTokens(account, clientId, clientSecret, onSignIn)
            } catch (e: RuntimeException) {
                Log.e(TAG, "Google Exchange Auth For Token Api Call Failed '${e.message}\nWith account:$account clientId:$clientId clientSecret:$clientSecret Auth Token:$authToken", e)
            }
        }
    }

    private fun fetchPhotos() {
        if (authToken.isNullOrEmpty()) return

        _isLoading.value = true

        viewModelScope.launch {
            try {
                val photos1 = googleAuthBusiness.createGooglePhotosApi(authToken!!).getPhotos()
                val mediaItems = photos1.mediaItems
                val photos = mediaItems.map { it.baseUrl }
                _photos.value = photos
            } catch (e: Exception) {
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
