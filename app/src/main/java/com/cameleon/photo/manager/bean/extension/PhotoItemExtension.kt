package com.cameleon.photo.manager.bean.extension

import com.cameleon.photo.manager.bean.PhotoItem
import com.cameleon.photo.manager.bean.PhotoSize

fun PhotoItem.urlBySize(size: PhotoSize) =
    when(size) {
        is PhotoSize.Min -> "${url}=w${size.width}-h${size.height}"
        is PhotoSize.Custom -> "${url}=w${size.width}-h${size.height}"
        is PhotoSize.Full -> urlFullSize
        else -> url
    }