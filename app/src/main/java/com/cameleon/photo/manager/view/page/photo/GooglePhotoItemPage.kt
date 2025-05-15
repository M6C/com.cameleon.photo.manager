package com.cameleon.photo.manager.view.page.photo

import android.annotation.SuppressLint
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.cameleon.photo.manager.R
import com.cameleon.photo.manager.ui.theme.PhotoManagerTheme

@SuppressLint("UnusedBoxWithConstraintsScope")
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

    // État pour la transformation (zoom et panoramique)
    val scale = remember { mutableStateOf(1f) }
    val offsetY = remember { mutableStateOf(0f) }

    // État transformable pour gérer les gestes
    val state = rememberTransformableState { zoomChange, offsetChange, _ ->
        // Limiter le zoom entre 1x et 3x
        scale.value = (scale.value * zoomChange).coerceIn(1f, 3f)

        // Permettre uniquement le défilement vertical
        if (scale.value > 1f) {
            offsetY.value += offsetChange.y
        }
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                    translationY = offsetY.value
                }
                .transformable(state = state)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GooglePhotoItemPagePreview() {
    // Ne passe aucun paramètre pour tester le fallback
    PhotoManagerTheme { GooglePhotoItemPage() }

}