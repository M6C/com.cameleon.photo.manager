package com.cameleon.photo.manager.business

import android.util.Log
import com.cameleon.photo.manager.api.GooglePhotosApi
import com.cameleon.photo.manager.api.MediaItem
import com.google.gson.Gson
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

data class PhotoItem(val url: String, val urlFullSize: String = url, val creationTime: String = "", val mimeType: String = "", val width: String = "", val height: String = "")

fun MediaItem.toPhotoItem() = PhotoItem (
    url = baseUrl,
    urlFullSize = "${baseUrl}=w${mediaMetadata.width}-h${mediaMetadata.height}",
    creationTime = mediaMetadata.creationTime,
    mimeType = mimeType,
    width = mediaMetadata.width,
    height = mediaMetadata.height,
)

fun PhotoItem.urlBySize(size: PhotoSize) =
    when(size) {
        is PhotoSize.Min -> "${url}=w${size.width}-h${size.height}"
        is PhotoSize.Custom -> "${url}=w${size.width}-h${size.height}"
        is PhotoSize.Full -> urlFullSize
        else -> url
    }

sealed class PhotoSize() {
    data class Custom(val width: Int, val height: Int) : PhotoSize()
    data object Min : PhotoSize() {
        val width: Int = 120
        val height: Int = 120
    }
    data object Full : PhotoSize()
}

class GooglePhotoBusiness @Inject constructor(private val googlePhotosApi: GooglePhotosApi) {

    companion object {
        private val TAG = GooglePhotoBusiness::class.simpleName
    }

    private var nextPageToken = ""


    suspend fun fetchPhotos(pageSize: Int = 50, throwsException: List<Class<*>> = emptyList()) = flow<List<PhotoItem>> {
        fetchMediaItems<PhotoItem>(pageSize, throwsException)
        { items : List<MediaItem> ->
            val l = items
                .map { it.toPhotoItem() }
            this@flow.emit(l)
        }
    }

    private suspend fun <T> fetchMediaItems(pageSize: Int = 50, throwsException: List<Class<*>> = emptyList(), mediaItemMap: suspend (List<MediaItem>) -> Unit = { emptyList<MediaItem>() }) {
        var json = ""
        try {
            val response = googlePhotosApi.getPhotos(pageSize, nextPageToken)
            json = Gson().toJson(response)
            nextPageToken = response.nextPageToken
            val listUrl = mediaItemMap(response.mediaItems)
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