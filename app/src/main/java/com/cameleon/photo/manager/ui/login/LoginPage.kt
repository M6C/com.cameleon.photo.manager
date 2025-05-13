package com.cameleon.photo.manager.ui.login


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.cameleon.photo.manager.ui.theme.PhotoManagerTheme

@Composable
fun LoginPage(modifier: Modifier = Modifier, onLoginClicked: () -> Unit) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Button(onClick = onLoginClicked) {
            Text("Sign in with Google")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPagePreview() {
    PhotoManagerTheme {
        LoginPage() {}
    }
}