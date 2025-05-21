package com.cameleon.photo.manager.api

import com.cameleon.photo.manager.business.PhotoItem
import retrofit2.http.GET
import retrofit2.http.Query

data class MediaItem(val id: String, val productUrl: String, val baseUrl: String, val mimeType: String, val mediaMetadata: MediaMetadata, val filename: String)
data class MediaMetadata(val creationTime: String, val width: String, val height: String, val photo: PhotoMetadata)
data class PhotoMetadata(val cameraMake: String, val cameraModel: String, val focalLength: Double? = null, val apertureFNumber: Double? = null, val isoEquivalent: Int? = null, val exposureTime: String? = null)

data class PhotosResponse(val mediaItems: List<MediaItem>, val nextPageToken: String)

interface GooglePhotosApi {

    @GET("v1/mediaItems")
    suspend fun getPhotos(@Query("pageSize") pageSize: Int, @Query("pageToken") pageToken: String? = null): PhotosResponse
}
