package com.cameleon.photo.manager.view.page.photo

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.cameleon.photo.manager.business.PhotoItem


@Composable
fun GooglePhotosScreen(loadNextPhotoBefore: Int = 20, onUnAuthenticate: () -> Unit = {}, onClickItem: (PhotoItem) -> Unit = {}) {
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