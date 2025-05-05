package com.cameleon.photo.manager.view.page.photo

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel


@Composable
fun GooglePhotosScreen(loadNextPhotoBefore: Int = 20) {
    val viewModel: GooglePhotosViewModel = hiltViewModel()

    val onFetchMediaItems = { viewModel.fetchMediaItems() }
    val canLoadNextPhoto = { visibleItemsCount: Int ->
        visibleItemsCount >= viewModel.mediaItems.size - loadNextPhotoBefore && !viewModel.isLoading && viewModel.canLoadNextPage()
    }
    val isLoading = { viewModel.isLoading }

    GooglePhotosPage(
        token = viewModel.accessToken,
        mediaItems = viewModel.mediaItems,
        onFetchMediaItems = onFetchMediaItems,
        canLoadNextPhoto = canLoadNextPhoto,
        isLoading = isLoading
    )
}