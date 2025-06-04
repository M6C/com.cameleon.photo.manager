package com.cameleon.photo.manager.business

import android.util.Log
import com.cameleon.photo.manager.api.GooglePhotosApi
import com.cameleon.photo.manager.bean.dto.AlbumsResponse
import com.cameleon.photo.manager.bean.dto.GetResponse
import com.cameleon.photo.manager.bean.dto.MediaAlbumItem
import com.cameleon.photo.manager.bean.dto.MediaItem
import com.cameleon.photo.manager.bean.dto.PhotosResponse
import com.cameleon.photo.manager.bean.dto.extension.toAlbumItem
import com.cameleon.photo.manager.bean.dto.extension.toPhotoItem
import com.google.gson.Gson
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class GooglePhotoBusiness @Inject constructor() {

    companion object {
        private val TAG = GooglePhotoBusiness::class.simpleName
    }

    private var nextPageToken = ""

    @Inject
    lateinit var googlePhotosApi: GooglePhotosApi

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

    suspend fun fetchAlbums(pageSize: Int = 50, throwsException: List<Class<*>> = emptyList()) = flow {
        fetchAlbumItems(pageSize, throwsException)
        { items : List<MediaAlbumItem> ->
            items
                .map { it.toAlbumItem() }
                .run { this@flow.emit(this) }
        }
    }

    private suspend fun fetchMediaItems(pageSize: Int = 50, throwsException: List<Class<*>> = emptyList(), mediaItemMap: suspend (List<MediaItem>) -> Unit = { }) =
        fetchItems <PhotosResponse, MediaItem> (
            itemName = "PhotoItem",
            apiCall = { googlePhotosApi.getPhotos(pageSize, nextPageToken)},
            throwsException = throwsException,
            itemMap = mediaItemMap
        )

    private suspend fun fetchAlbumItems(pageSize: Int = 50, throwsException: List<Class<*>> = emptyList(), mediaItemMap: suspend (List<MediaAlbumItem>) -> Unit = { }) =
        fetchItems <AlbumsResponse, MediaAlbumItem> (
            itemName = "AlbumItem",
            apiCall = { googlePhotosApi.getAlbums(pageSize, nextPageToken)},
            throwsException = throwsException,
            itemMap = mediaItemMap
        )

    private suspend fun <R: GetResponse<T>, T> fetchItems(itemName: String, apiCall: suspend () -> R, throwsException: List<Class<*>> = emptyList(), itemMap: suspend (List<T>) -> Unit = { }) {
        var json = ""
        try {
            val response = apiCall()
            json = gson.toJson(response)
            nextPageToken = response.token
            itemMap(response.items)
        } catch (e: RuntimeException) {
            val exClass = e.javaClass
            val nameException = throwsException.map { it.toString() }
            Log.w(TAG, "Fetching $itemName Failed: ex:$exClass throwsException:${nameException.joinToString()}")
            if (nameException.contains(exClass.toString())) {
                Log.e(TAG, "Fetching $itemName Failed: throws exception $exClass", e)
                throw e
            }
            else if (exClass == HttpException::class.java) {
                val ex: HttpException = e as HttpException
                val errorBody = ex.response()?.errorBody()?.string()
                Log.e(TAG, "Fetching $itemName Failed: HTTP CODE:${ex.code()} - BODY:${errorBody}", e)
            }
            else {
                Log.e(TAG, "Fetching $itemName Failed with exception ${exClass}\nJson:$json", e)
            }
        }
    }

    fun canLoadNextPage() = nextPageToken.isNotEmpty()

    fun logOut() {
        nextPageToken = ""
    }
}