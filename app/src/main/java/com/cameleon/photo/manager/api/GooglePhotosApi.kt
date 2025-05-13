package com.cameleon.photo.manager.api

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

data class MediaItem(val baseUrl: String)
data class PhotosResponse(val mediaItems: List<MediaItem>, val nextPageToken: String)

interface GooglePhotosApi {

    @GET("v1/mediaItems")
    suspend fun getPhotos(@Query("pageSize") pageSize: Int, @Query("pageToken") pageToken: String? = null): PhotosResponse
}
