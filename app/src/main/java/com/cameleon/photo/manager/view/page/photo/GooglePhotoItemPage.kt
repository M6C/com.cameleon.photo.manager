package com.cameleon.photo.manager.view.page.photo

import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.cameleon.photo.manager.R

@Composable
fun GooglePhotoItemPage(
    url: String? = null,
    uri: Uri? = null,
    @DrawableRes imageResId: Int? = null,
    @DrawableRes fallbackResId: Int = R.drawable.placeholder // Image par défaut si rien n'est fourni
) {
    val context = LocalContext.current

    val painter = when {
        url != null -> rememberAsyncImagePainter(
            model = ImageRequest.Builder(context)
                .data(url)
                .crossfade(true)
                .build()
        )

        uri != null -> rememberAsyncImagePainter(
            model = ImageRequest.Builder(context)
                .data(uri)
                .crossfade(true)
                .build()
        )

        imageResId != null -> painterResource(id = imageResId)

        else -> painterResource(id = fallbackResId) // fallback ici
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GooglePhotoItemPagePreview() {
    // Ne passe aucun paramètre pour tester le fallback
    GooglePhotoItemPage()
}

