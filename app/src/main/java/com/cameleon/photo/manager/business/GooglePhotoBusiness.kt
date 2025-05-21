package com.cameleon.photo.manager.business

import android.util.Log
import com.cameleon.photo.manager.api.GooglePhotosApi
import com.cameleon.photo.manager.bean.dto.MediaItem
import com.cameleon.photo.manager.bean.dto.extension.toPhotoItem
import com.google.gson.Gson
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class GooglePhotoBusiness @Inject constructor(private val googlePhotosApi: GooglePhotosApi) {

    companion object {
        private val TAG = GooglePhotoBusiness::class.simpleName
    }

    private var nextPageToken = ""

    @Inject
    lateinit var gson: Gson

    suspend fun fetchPhotos(pageSize: Int = 50, throwsException: List<Class<*>> = emptyList()) = flow {
        fetchMediaItems(pageSize, throwsException)
            { items : List<MediaItem> ->
                items
                    .map { it.toPhotoItem() }
                    .run { this@flow.emit(this) }
            }
    }

    private suspend fun fetchMediaItems(pageSize: Int = 50, throwsException: List<Class<*>> = emptyList(), mediaItemMap: suspend (List<MediaItem>) -> Unit = { }) {
        var json = ""
        try {
            val response = googlePhotosApi.getPhotos(pageSize, nextPageToken)
            json = gson.toJson(response)
            nextPageToken = response.nextPageToken
            mediaItemMap(response.mediaItems)
        } catch (e: RuntimeException) {
            val exClass = e.javaClass
            val nameException = throwsException.map { it.toString() }
            Log.w(TAG, "Fetching Images Failed: ex:$exClass throwsException:${nameException.joinToString()}")
            if (nameException.contains(exClass.toString())) {
                Log.e(TAG, "Fetching Images Failed: throws exception $exClass", e)
                throw e
            }
            else if (exClass == HttpException::class.java) {
                val ex: HttpException = e as HttpException
                val errorBody = ex.response()?.errorBody()?.string()
                Log.e(TAG, "Fetching Images Failed: HTTP CODE:${ex.code()} - BODY:${errorBody}", e)
            }
            else {
                Log.e(TAG, "Fetching Images Failed with exception ${exClass}\nJson:$json", e)
            }
        }
    }

    fun canLoadNextPage() = nextPageToken.isNotEmpty()

    fun logOut() {
        nextPageToken = ""
    }
}