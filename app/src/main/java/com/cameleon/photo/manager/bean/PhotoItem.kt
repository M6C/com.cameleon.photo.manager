package com.cameleon.photo.manager.bean

data class PhotoItem(val url: String, val urlFullSize: String = url, val creationTime: String = "", val mimeType: String = "", val width: String = "", val height: String = "")