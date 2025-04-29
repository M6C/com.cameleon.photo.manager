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
//    var nextPageToken by mutableStateOf("")
//        private set
    var isLoading by mutableStateOf(false)
        private set

    fun getAccessToken() = tokenBusiness.getAccessToken()

    fun canLoadNextPage() = false//googlePhotoBusiness.canLoadNextPage()

    fun fetchMediaItems(pageSize: Int = 50) {
        viewModelScope.launch(Dispatchers.IO) {
            googlePhotoBusiness.fetchMediaItems(getAccessToken(), pageSize).collect {urls ->
                mediaItems = mediaItems + urls
            }
        }
    }

//    fun fetchMediaItems(token: String, pageSize: Int = 50) {
//        if (isLoading || token.isEmpty()) return
//        isLoading = true
//        viewModelScope.launch(Dispatchers.IO) {
//            var json = ""
//            try {
//                val url = URL("https://photoslibrary.googleapis.com/v1/mediaItems?pageSize=$pageSize&pageToken=$nextPageToken")
//                val connection = url.openConnection() as HttpsURLConnection
//                connection.requestMethod = "GET"
//                connection.setRequestProperty("Authorization", "Bearer $token")
//                Log.i("GooglePhotos", "Fetch Next $pageSize Media. Current Media Size:${mediaItems.size}")
//
//                val responseCode = connection.responseCode
//                if (responseCode == HttpURLConnection.HTTP_OK) {
//                    val response = connection.inputStream.bufferedReader().use { it.readText() }
//                    json = response
//                    val jsonObject = JSONObject(response)
//                    try {
//                        val items = jsonObject.getJSONArray("mediaItems")
//                        val urls = (0 until items.length()).map { index ->
//                            items.getJSONObject(index).getString("baseUrl")
//                        }
//                        mediaItems = mediaItems + urls
//                    } catch (e: Exception) {
//                        Log.w("GooglePhotos", "${e.message} Json:$json")
//                    }
//                    nextPageToken = jsonObject.optString("nextPageToken", "")
//                } else {
//                    Log.e("GooglePhotos", "Error fetching images responseCode:$responseCode")
//                }
//            } catch (e: Exception) {
//                Log.e("GooglePhotos", "Error fetching images Json:$json", e)
//            }
//            isLoading = false
//        }
//    }

    fun logOut() {
        mediaItems = emptyList()
//        nextPageToken = ""
        isLoading = false
        googlePhotoBusiness.logOut()
    }
}