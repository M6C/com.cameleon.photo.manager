package com.cameleon.photo.manager.bean.dto.extension

import com.cameleon.photo.manager.bean.PhotoItem
import com.cameleon.photo.manager.bean.dto.MediaItem

fun MediaItem.toPhotoItem() = PhotoItem(
    url = baseUrl,
    urlFullSize = "${baseUrl}=w${mediaMetadata.width}-h${mediaMetadata.height}",
    creationTime = mediaMetadata.creationTime,
    mimeType = mimeType,
    width = mediaMetadata.width,
    height = mediaMetadata.height,
)