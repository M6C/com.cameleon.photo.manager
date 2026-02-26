package com.cameleon.photo.manager.ui.topbar

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cameleon.photo.manager.bean.dto.UserInfoResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarComponent(
        isSignedIn: Boolean,
        userInfo: UserInfoResponse? = null,
        onLogoutClick: () -> Unit
) {
    TopAppBar(
            title = {
                Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 8.dp)
                ) {
                    if (userInfo?.picture != null) {
                        AsyncImage(
                                model = userInfo.picture,
                                contentDescription = "Photo de profil",
                                modifier = Modifier.size(32.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Column {
                        Text(
                                text = "Google Photos",
                                fontSize = 18.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                        )
                        if (userInfo != null) {
                            Text(
                                    text = userInfo.name ?: "",
                                    fontSize = 12.sp,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            },
            actions = {
                Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(
                            text = if (isSignedIn) "✅ Connecté" else "❌ Déconnecté",
                            fontSize = 14.sp,
                            modifier = Modifier.padding(end = 8.dp)
                    )

                    Button(
                            onClick = onLogoutClick,
                    ) {
                        Text(
                                text = if (isSignedIn) "Logout" else "Login",
                                color = if (isSignedIn) Color.White else Color.Black
                        )
                    }
                }
            }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewTopAppBarComponent() {
    TopAppBarComponent(isSignedIn = true) {
        // Simulate logout action
        Log.d("TopAppBarPreview", "Logout clicked!")
    }
}
