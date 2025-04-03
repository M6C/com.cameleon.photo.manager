package com.cameleon.photo.manager.view.page.photo

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.flow.distinctUntilChanged


@Composable
fun GooglePhotosScreen(viewModel: GooglePhotosViewModel, loadNextPhotoBefore: Int = 5) {
    val token by remember { mutableStateOf(viewModel.getAccessToken() ?: "") }

    LaunchedEffect(token) {
        // Fetch 1st Photo only on 1st Componnent Composition
        if (token.isNotEmpty()) {
            viewModel.fetchMediaItems(token)
        }
    }

    val listState = rememberLazyStaggeredGridState()
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex + listState.layoutInfo.visibleItemsInfo.size }
            .distinctUntilChanged()
            .collect { visibleItemsCount ->
                if (visibleItemsCount >= viewModel.mediaItems.size - loadNextPhotoBefore && !viewModel.isLoading && viewModel.nextPageToken.isNotEmpty()) {
                    viewModel.fetchMediaItems(token)
                }
            }
    }

    Column(modifier = Modifier
        .fillMaxSize()/*
        .padding(16.dp)*/) {
        if (viewModel.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Adaptive(minSize = 100.dp), state = listState) {
            items(viewModel.mediaItems.size) { index -> val url = viewModel.mediaItems[index]
                Image(
                    painter = rememberAsyncImagePainter(url),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
//            item {
//                if (viewModel.isLoading) {
//                    Box(
//                        modifier = Modifier.fillMaxWidth(),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
//                    }
//                }
//            }

        }
    }
}
