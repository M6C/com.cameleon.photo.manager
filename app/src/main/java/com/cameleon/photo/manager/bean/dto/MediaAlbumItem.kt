package com.cameleon.photo.manager.bean.dto

data class MediaAlbumItem(val id: String, val title: String, val productUrl: String, val isWritable: Boolean, val shareInfo: ShareInfo, val mediaItemsCount: String, val coverPhotoBaseUrl: String, val coverPhotoMediaItemId: String)

data class ShareInfo(val sharedAlbumOptions: SharedAlbumOptions, val shareableUrl: String, val shareToken: String, val isJoined: Boolean, val isOwned: Boolean, val isJoinable: Boolean)

data class SharedAlbumOptions(val isCollaborative: Boolean, val isCommentable: Boolean)
