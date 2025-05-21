package com.cameleon.photo.manager.view.page.photo

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.cameleon.photo.manager.R
import com.cameleon.photo.manager.bean.PhotoItem
import com.cameleon.photo.manager.bean.PhotoSize
import com.cameleon.photo.manager.bean.extension.urlBySize
import com.cameleon.photo.manager.ui.theme.PhotoManagerTheme
import kotlinx.coroutines.flow.distinctUntilChanged


@Composable
fun GooglePhotosPage(
    token: MutableState<String>,
    mediaItems: List<PhotoItem>,
    canLoadNextPhoto: (visibleItemsCount: Int) -> Boolean,
    onFetchMediaItems: () -> Unit,
    isLoading: () -> Boolean,
    onClickItem: (PhotoItem) -> Unit
) {
    val currentToken = token.value
    val listState = rememberLazyStaggeredGridState()

    // Chargement initial
    LaunchedEffect(currentToken) {
        if (currentToken.isNotEmpty() && (mediaItems.isEmpty() || canLoadNextPhoto(listState.firstVisibleItemIndex))) {
            onFetchMediaItems()
        }
    }

    // Scroll infini
    LaunchedEffect(listState) {
        snapshotFlow {
            listState.firstVisibleItemIndex + listState.layoutInfo.visibleItemsInfo.size
        }.distinctUntilChanged().collect { visibleItemsCount ->
            if (canLoadNextPhoto(visibleItemsCount)) {
                onFetchMediaItems()
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (isLoading()) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        val photoSize = PhotoSize.Min()

        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(photoSize.nbColumn), // Meilleure stabilité de perf que Adaptive
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalItemSpacing = 6.dp,
            modifier = Modifier.fillMaxSize()
        ) {
            items(mediaItems.size) { id ->
                val photoItem = mediaItems[id]
                val imageUrl = photoItem.urlBySize(photoSize)

                Surface(
                    modifier = Modifier
                        .aspectRatio(1f),
                    tonalElevation = 2.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(imageUrl)
                                    .crossfade(true)
                                    .size(photoSize.width)
                                    .placeholder(R.drawable.placeholder)
                                    .error(R.drawable.error_image)
                                    .build()
                            ),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { onClickItem(photoItem) }
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
fun GooglePhotosPagePreview() {
    PhotoManagerTheme {
        GooglePhotosPage(
            token = mutableStateOf("demo-token"),
            mediaItems = List(30) { PhotoItem(url = "https://via.placeholder.com/150") },
            canLoadNextPhoto = { true },
            onFetchMediaItems = {},
            isLoading = { false },
            onClickItem = {}
        )
    }
}
