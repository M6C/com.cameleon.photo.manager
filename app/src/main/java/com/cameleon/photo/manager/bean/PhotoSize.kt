package com.cameleon.photo.manager.bean

sealed class PhotoSize {
    data class Custom(val width: Int, val height: Int) : PhotoSize()
    data class Min(val width: Int = 200, val height: Int = 200, val nbColumn: Int = 3) : PhotoSize()
    data object Full : PhotoSize()
}