package com.cameleon.photo.manager.ui.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cameleon.photo.manager.R
import com.cameleon.photo.manager.bean.dto.UserInfoResponse
import com.cameleon.photo.manager.ui.theme.PhotoManagerTheme

@Composable
fun LoginPage(
        modifier: Modifier = Modifier,
        userInfo: UserInfoResponse? = null,
        onLoginClicked: () -> Unit
) {
    val context = LocalContext.current
    val serverClientId = remember { context.getString(R.string.server_client_id) }
    val clientSecret = remember { context.getString(R.string.client_secret) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (userInfo?.picture != null) {
                AsyncImage(
                        model = userInfo.picture,
                        contentDescription = "Photo de profil",
                        modifier = Modifier.size(80.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Nom et email utilisateur
            if (userInfo != null) {
                Text(
                        text = userInfo.name ?: "Utilisateur inconnu",
                        style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                        text = userInfo.email ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            Button(onClick = onLoginClicked) {
                Text(if (userInfo != null) "Reconnexion" else "Sign in with Google")
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text("Debug Info:", style = MaterialTheme.typography.titleMedium)
            Text("Client ID: ${maskString(serverClientId, 20, 30)}", fontSize = 10.sp)
            Text("Client Secret: ${maskString(clientSecret, 10, 10)}", fontSize = 10.sp)
        }
    }
}

fun maskString(input: String, visibleCharsStart: Int = 10, visibleCharsEnd: Int = 10): String {
    if (input.length <= (visibleCharsStart + visibleCharsEnd)) return input
    return "${input.take(visibleCharsStart)}...${input.takeLast(visibleCharsEnd)}"
}

@Preview(showBackground = true)
@Composable
fun LoginPagePreview() {
    PhotoManagerTheme {
        LoginPage(
                userInfo =
                        UserInfoResponse(
                                id = "123",
                                email = "john.doe@gmail.com",
                                name = "John Doe",
                                picture = null
                        )
        ) {}
    }
}
