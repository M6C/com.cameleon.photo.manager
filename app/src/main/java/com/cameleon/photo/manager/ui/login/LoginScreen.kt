package com.cameleon.photo.manager.ui.login


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.cameleon.photo.manager.extension.getActivity
import com.cameleon.photo.manager.ui.theme.PhotoManagerTheme

@Composable
fun LoginScreen(onLoginClicked: () -> Unit = {}) {

    val context = LocalContext.current
    val activity = context.getActivity()

    if (activity != null) {
        LoginPage(onLoginClicked = onLoginClicked)
    } else {
        ErrorMessage("Activity No Initialized !")
    }
}

@Composable
fun ErrorMessage(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(message)
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    PhotoManagerTheme {
        LoginScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorMessagePreview() {
    PhotoManagerTheme {
        ErrorMessage("Activity No Initialized !")
    }
}