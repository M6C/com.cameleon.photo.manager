package com.cameleon.photo.manager.view.page.photo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cameleon.photo.manager.business.GooglePhotoBusiness
import com.cameleon.photo.manager.business.TokenBusiness
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GooglePhotosViewModel @Inject constructor(private val tokenBusiness: TokenBusiness, private val googlePhotoBusiness: GooglePhotoBusiness) : ViewModel() {
    var mediaItems by mutableStateOf<List<String>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set

    var accessToken = mutableStateOf(tokenBusiness.getAccessToken() ?: "")
        private set

    private fun getAccessToken() = accessToken.value

    fun canLoadNextPage() = googlePhotoBusiness.canLoadNextPage()

    fun fetchMediaItems(pageSize: Int = 50) {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading = true
            try {
                googlePhotoBusiness.fetchMediaItems(getAccessToken(), pageSize).collect {urls ->
                    mediaItems = mediaItems + urls
                    isLoading = false
                }
            } catch (ex: RuntimeException) {
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