package com.cameleon.photo.manager.view.page.photo

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.cameleon.photo.manager.business.PhotoItem
import com.cameleon.photo.manager.business.PhotoSize
import com.cameleon.photo.manager.business.urlBySize
import com.cameleon.photo.manager.ui.theme.PhotoManagerTheme
import kotlinx.coroutines.flow.distinctUntilChanged


@Composable
fun GooglePhotosPage(token: MutableState<String>, mediaItems: List<PhotoItem>, canLoadNextPhoto: (visibleItemsCount: Int) -> Boolean, onFetchMediaItems: () -> Unit, isLoading: () -> Boolean, onClickItem: (PhotoItem) -> Unit) {
    val rememberToken by remember { token }
    val listState = rememberLazyStaggeredGridState()

    LaunchedEffect(rememberToken) {
        // Fetch 1st Photo only on 1st Component Composition
        if (rememberToken.isNotEmpty() && (mediaItems.isEmpty() || canLoadNextPhoto(listState.firstVisibleItemIndex))) {
            onFetchMediaItems()
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex + listState.layoutInfo.visibleItemsInfo.size }
            .distinctUntilChanged()
            .collect { visibleItemsCount ->
                if (canLoadNextPhoto(visibleItemsCount)) {
                    onFetchMediaItems()
                }
            }
    }

//    val photos = mediaItems.mapIndexed { i: Int, p: PhotoItem ->
//        Photo(i, p.urlBySize(PhotoSize.Min))
//    }
//    val selectedIds: MutableState<Set<Int>> = rememberSaveable { mutableStateOf(emptySet()) }
//
//    Column(modifier = Modifier.fillMaxSize()) {
//        if (isLoading()) {
//            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
//        }
//        Spacer(modifier = Modifier.height(16.dp))
//        PhotosGrid(photos = photos, selectedIds = selectedIds, onClickPhoto = { it -> onClickItem(PhotoItem(url = it))})
//    }


    Column(modifier = Modifier
        .fillMaxSize()/*
        .padding(16.dp)*/) {
        if (isLoading()) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(minSize = PhotoSize.Min.width.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            state = listState,
        ) {
            items(mediaItems.size) { index -> val url = mediaItems[index]
                Surface(
                    modifier = Modifier.aspectRatio(1f),
                    tonalElevation = 3.dp
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(url.urlBySize(PhotoSize.Min)),
                            contentDescription = null,
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier
//                                .aspectRatio(1f)
                                .fillMaxWidth()
//                                .height(200.dp)
                                .clickable { onClickItem(url) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
fun GooglePhotosPagePreview() {
    PhotoManagerTheme { GooglePhotosPage(mutableStateOf(value = ""), buildList(200) { add(PhotoItem("", "")) }, {true}, {}, {true}, {}) }
}