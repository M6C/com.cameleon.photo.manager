package com.cameleon.photo.manager.business

import android.util.Log
import com.cameleon.photo.manager.api.GooglePhotosApi
import com.google.gson.Gson
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class GooglePhotoBusiness @Inject constructor(private val googlePhotosApi: GooglePhotosApi) {

    companion object {
        private val TAG = GooglePhotoBusiness::class.simpleName
    }

    private var nextPageToken = ""

    // TODO User Token to retrive Photo
    suspend fun fetchMediaItems(pageSize: Int = 50, throwsException: List<Class<*>> = emptyList()) = flow {
        var json = ""
        try {
            val response = googlePhotosApi.getPhotos(pageSize, nextPageToken)
            json = Gson().toJson(response)
            nextPageToken = response.nextPageToken
            emit( response.mediaItems.map { it.baseUrl } )
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