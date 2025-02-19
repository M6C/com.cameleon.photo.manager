package com.cameleon.photo.manager.api

import retrofit2.http.GET
import retrofit2.http.Query

data class MediaItem(val baseUrl: String)
data class PhotosResponse(val mediaItems: List<MediaItem>)

interface GooglePhotosApi {
    @GET("v1/mediaItems")
    suspend fun getPhotos(@Query("pageSize") pageSize: Int = 50): PhotosResponse
}
