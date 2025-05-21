package com.cameleon.photo.manager.bean.dto

data class MediaItem(val id: String, val productUrl: String, val baseUrl: String, val mimeType: String, val mediaMetadata: MediaMetadata, val filename: String)