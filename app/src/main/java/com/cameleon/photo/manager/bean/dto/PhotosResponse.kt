package com.cameleon.photo.manager.bean.dto

open class GetResponse <I> (val items: List<I>, val token: String)

data class PhotosResponse(val mediaItems: List<MediaItem>, val nextPageToken: String) : GetResponse<MediaItem>(mediaItems, nextPageToken)