package com.cameleon.photo.manager.bean.dto

data class AlbumsResponse(val mediaItems: List<MediaAlbumItem>, val nextPageToken: String) : GetResponse<MediaAlbumItem>(mediaItems, nextPageToken)