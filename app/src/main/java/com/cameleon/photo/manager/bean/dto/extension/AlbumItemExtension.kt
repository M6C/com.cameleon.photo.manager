package com.cameleon.photo.manager.bean.dto.extension

import com.cameleon.photo.manager.bean.AlbumItem
import com.cameleon.photo.manager.bean.dto.MediaAlbumItem

fun MediaAlbumItem.toAlbumItem() = AlbumItem (
    id = id,
    name = this.title,
    url = this.productUrl,
    creationTime = "",
)