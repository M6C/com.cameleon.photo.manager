package com.cameleon.photo.manager.view.page.photo

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter


@Composable
fun GooglePhotoItemPage(url: String) {

    Column(modifier = Modifier
        .fillMaxSize()/*
        .padding(16.dp)*/) {
//        if (isLoading()) {
//            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
//        }
        Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = rememberAsyncImagePainter(url),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
//                Spacer(modifier = Modifier.height(8.dp))
    }
}
