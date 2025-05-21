package com.cameleon.photo.manager.view.page.photo

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cameleon.photo.manager.business.GooglePhotoBusiness
import com.cameleon.photo.manager.business.PhotoItem
import com.cameleon.photo.manager.business.TokenBusiness
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class GooglePhotosViewModel @Inject constructor(private val tokenBusiness: TokenBusiness, private val googlePhotoBusiness: GooglePhotoBusiness) : ViewModel() {

    companion object {
        private val TAG = GooglePhotosViewModel::class.simpleName
    }

    var mediaItems by mutableStateOf<List<PhotoItem>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set

    var accessToken = mutableStateOf(tokenBusiness.getAccessToken() ?: "")
        private set

    fun canLoadNextPage() = googlePhotoBusiness.canLoadNextPage()

    fun fetchMediaItems(pageSize: Int = 50, onUnAuthenticate: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading = true
            try {
                googlePhotoBusiness.fetchPhotos(pageSize, throwsException = listOf(HttpException::class.java)).collect { urls ->
                    mediaItems = mediaItems + urls
                    isLoading = false
                }
            } catch (ex: HttpException) {
                val errorBody = ex.response()?.errorBody()?.string()
                Log.e(TAG, "Fetching Images Failed: HTTP ${ex.code()} - $errorBody", ex)
                onUnAuthenticate()
            }
            finally {
                isLoading = false
            }
        }
    }

    fun logOut() {
        mediaItems = emptyList()
        isLoading = false
        googlePhotoBusiness.logOut()
    }
}