package com.cameleon.photo.manager.ui.topbar

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarComponent(
    isSignedIn: Boolean,
    onLogoutClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text("Google Photos", fontSize = 20.sp)
        },
        actions = {
            Text(
                text = if (isSignedIn) "✅ Connecté" else "❌ Déconnecté",
                color = if (isSignedIn) Color.Green else Color.Red,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .align(Alignment.CenterVertically)
            )
            Button(
                onClick = onLogoutClick,
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Text(
                    text = if (isSignedIn) "❌" else "✅",
                    color = if (isSignedIn) Color.White else Color.Black
                )
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
