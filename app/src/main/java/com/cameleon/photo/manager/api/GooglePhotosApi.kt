package com.cameleon.photo.manager.api

import com.cameleon.photo.manager.bean.dto.AlbumsResponse
import com.cameleon.photo.manager.bean.dto.PhotosResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GooglePhotosApi {

    @GET("v1/mediaItems")
    suspend fun getPhotos(@Query("pageSize") pageSize: Int, @Query("pageToken") pageToken: String? = null): PhotosResponse

    @GET("v1/albums")
    suspend fun getAlbums(@Query("pageSize") pageSize: Int, @Query("pageToken") pageToken: String? = null, @Query("excludeNonAppCreatedData") excludeNonApp: Boolean? = null): AlbumsResponse
}
