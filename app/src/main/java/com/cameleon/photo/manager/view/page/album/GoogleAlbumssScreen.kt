package com.cameleon.photo.manager.view.page.album

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.cameleon.photo.manager.bean.AlbumItem
import com.cameleon.photo.manager.view.page.photo.GooglePhotosPage
import com.cameleon.photo.manager.view.page.photo.GooglePhotosViewModel


@Composable
fun GoogleAlbumsScreen(loadNextPhotoBefore: Int = 20, onUnAuthenticate: () -> Unit = {}, onClickItem: (AlbumItem) -> Unit = {}) {
    val viewModel: GooglePhotosViewModel = hiltViewModel()

    val onFetchMediaItems = { viewModel.fetchMediaItems(onUnAuthenticate = onUnAuthenticate) }
    val canLoadNextPhoto = { visibleItemsCount: Int ->
        visibleItemsCount >= viewModel.mediaItems.size - loadNextPhotoBefore && !viewModel.isLoading && viewModel.canLoadNextPage()
    }
    val isLoading = { viewModel.isLoading }

    GooglePhotosPage(
        token = viewModel.accessToken,
        mediaItems = viewModel.mediaItems,
        onClickItem = onClickItem,
        onFetchMediaItems = onFetchMediaItems,
        canLoadNextPhoto = canLoadNextPhoto,
        isLoading = isLoading
    )
}