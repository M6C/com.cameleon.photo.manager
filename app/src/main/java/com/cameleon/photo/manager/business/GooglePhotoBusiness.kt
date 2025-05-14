package com.cameleon.photo.manager.business

import android.util.Log
import com.cameleon.photo.manager.api.GooglePhotosApi
import com.cameleon.photo.manager.ui.activity.MainActivity.Companion.TAG
import com.google.gson.Gson
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class GooglePhotoBusiness @Inject constructor(private val googlePhotosApi: GooglePhotosApi) {

    private var nextPageToken = ""

    // TODO User Token to retrive Photo
    suspend fun fetchMediaItems(token: String?, pageSize: Int = 50) = flow {
        var json = ""
        try {
            val response = googlePhotosApi.getPhotos(pageSize, nextPageToken)
            json = Gson().toJson(response)
            nextPageToken = response.nextPageToken
            emit( response.mediaItems.map { it.baseUrl } )
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e(TAG, "Fetching Images Failed: HTTP ${e.code()} - $errorBody", e)
        } catch (e: Exception) {
            Log.e(TAG, "Fetching Images Failed with Json:$json", e)
        }
    }

    fun canLoadNextPage() = nextPageToken.isNotEmpty()

    fun logOut() {
        nextPageToken = ""
    }
}