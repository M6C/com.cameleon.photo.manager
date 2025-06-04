package com.cameleon.photo.manager.view.page.album

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cameleon.photo.manager.bean.AlbumItem
import com.cameleon.photo.manager.business.GooglePhotoBusiness
import com.cameleon.photo.manager.business.TokenBusiness
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class GoogleAlbumsViewModel @Inject constructor(tokenBusiness: TokenBusiness) : ViewModel() {

    companion object {
        private val TAG = GoogleAlbumsViewModel::class.simpleName
    }

    @Inject
    lateinit var googlePhotoBusiness: GooglePhotoBusiness

    var items by mutableStateOf<List<AlbumItem>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set

    var accessToken = mutableStateOf(tokenBusiness.getAccessToken() ?: "")
        private set

    fun canLoadNextPage() = googlePhotoBusiness.canLoadNextPage()

    fun fetchItems(pageSize: Int = 50, onUnAuthenticate: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading = true
            try {
                googlePhotoBusiness.fetchAlbums(pageSize, throwsException = listOf(HttpException::class.java)).collect { urls ->
                    items = items + urls
                    isLoading = false
                }
            } catch (ex: HttpException) {
                val errorBody = ex.response()?.errorBody()?.string()
                Log.e(TAG, "Fetching Albums Failed: HTTP ${ex.code()} - $errorBody", ex)
                onUnAuthenticate()
            }
            finally {
                isLoading = false
            }
        }
    }

    fun logOut() {
        items = emptyList()
        isLoading = false
        googlePhotoBusiness.logOut()
    }
}