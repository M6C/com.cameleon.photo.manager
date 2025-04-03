package com.cameleon.photo.manager.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cameleon.photo.manager.ui.theme.PhotoManagerTheme
import com.cameleon.photo.manager.view.page.photo.GooglePhotosScreen
import com.cameleon.photo.manager.view.page.photo.GooglePhotosViewModel
import com.cameleon.photo.manager.view.page.photo.PhotosViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
         val TAG = MainActivity::class.simpleName
    }

    private val viewModel: PhotosViewModel by viewModels()

    private val viewModelPhoto: GooglePhotosViewModel by viewModels()

    @Inject
    lateinit var googleSignInOptions : GoogleSignInOptions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.singIn(this) {
            Log.i(TAG, "-----> viewModel.singIn onSingIn Callback")
        }

        enableEdgeToEdge()
        setContent {
            val photos = viewModel.photos.collectAsState().value

            viewModel.getUserMessage()?.let {
                Toast.makeText(applicationContext, "Message : $it", Toast.LENGTH_SHORT).show()
            }
            viewModel.getUserError()?.let {
                Toast.makeText(applicationContext, "Error : $it", Toast.LENGTH_SHORT).show()
            }

            PhotoManagerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val isSignedIn = viewModel.isSignedIn.collectAsState()

                    Column(modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()) {

                        Row() {
                            Text("Google Photos", fontSize = 24.sp, modifier = Modifier.padding(16.dp))
                            Spacer(modifier = Modifier.weight(1f))
                            Button(onClick = {
                                viewModel.logOut()
                                viewModelPhoto.logOut()
                                Toast.makeText(this@MainActivity, "Logout Successful", Toast.LENGTH_SHORT).show()
                            }) {
                                Text(text = "Logout")
                            }
                        }

                        if (isSignedIn.value) {
                            GooglePhotosScreen(viewModelPhoto)
                        } else {
                            LoginScreen(onLoginClicked = {
                                viewModel.launchSingIn(this@MainActivity)
                            })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoginScreen(onLoginClicked: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Button(onClick = onLoginClicked) {
            Text("Sign in with Google")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    PhotoManagerTheme {
        LoginScreen() {}
    }
}