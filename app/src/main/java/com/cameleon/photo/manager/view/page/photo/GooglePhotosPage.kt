package com.cameleon.photo.manager.view.page.photo

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.flow.distinctUntilChanged


@Composable
fun GooglePhotosPage(token: MutableState<String>, mediaItems: List<String>, canLoadNextPhoto: (visibleItemsCount: Int) -> Boolean, onFetchMediaItems: () -> Unit, isLoading: () -> Boolean) {
    val token by remember { token }

    LaunchedEffect(token) {
        // Fetch 1st Photo only on 1st Componnent Composition
        if (token.isNotEmpty()) {
            onFetchMediaItems()
        }
    }

    val listState = rememberLazyStaggeredGridState()
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex + listState.layoutInfo.visibleItemsInfo.size }
            .distinctUntilChanged()
            .collect { visibleItemsCount ->
                if (canLoadNextPhoto(visibleItemsCount)) {
                    onFetchMediaItems()
                }
            }
    }

    Column(modifier = Modifier
        .fillMaxSize()/*
        .padding(16.dp)*/) {
        if (isLoading()) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Adaptive(minSize = 100.dp), state = listState) {
            items(mediaItems.size) { index -> val url = mediaItems[index]
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
//                if (isLoading()) {
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
