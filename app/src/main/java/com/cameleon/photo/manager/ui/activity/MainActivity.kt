package com.cameleon.photo.manager.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import com.cameleon.photo.manager.R
import com.cameleon.photo.manager.ui.theme.PhotoManagerTheme
import com.cameleon.photo.manager.view.page.photo.PhotosViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
         val TAG = MainActivity::class.simpleName
    }

    private val viewModel: PhotosViewModel by viewModels()

    @Inject
    lateinit var googleSignInOptions : GoogleSignInOptions


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        // Google Sign-In configuration
        val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleSignInResult(task)
        }


        enableEdgeToEdge()
        setContent {
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
                                Toast.makeText(this@MainActivity, "Logout Successful", Toast.LENGTH_SHORT).show()
                            }) {
                                Text(text = "Logout")
                            }
                        }

//                        if (isSignedIn.value) {
//                            GooglePhotosScreen(viewModelPhoto)
//                        } else {
                            LoginScreen(onLoginClicked = {
                                val client = GoogleSignIn.getClient(this@MainActivity, googleSignInOptions)
                                signInLauncher.launch(client.signInIntent)
                            })
//                        }
                    }
                }
            }
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            viewModel.exchangeAuthCodeForTokens(task.result, getString(R.string.server_client_id), getString(R.string.client_secret)) {
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
            }
        } catch (e: RuntimeException) {
            when {
                e.message?.contains("com.google.android.gms.common.api.ApiException") ?: false -> {

                    val code = e.message?.let { str ->
                        Regex("[0-9]+").findAll(str).lastOrNull()?.value?.toInt()
                    } ?: -1

                    when (code) {
                        7 -> {
                            Log.e(TAG, "Internet Connection Error", e)
                            Toast.makeText(this, "Sign-in failed - ApiException - Internet Connection Error : ${e.message}", Toast.LENGTH_LONG).show()
                        }
                        10 -> {
                            Log.e(TAG, "Certain Google Play services (such as Google Sign-in and App Invites) require you to provide the SHA-1 of your signing certificate so we can create an OAuth2 client and API key for your app\nhttps://console.cloud.google.com/apis/credentials", e)
                            Toast.makeText(this, "Sign-in failed - ApiException - SHA-1 of signing certificate Required in Google Cloud Console. Create an OAuth2 client and API key for your app : ${e.message}", Toast.LENGTH_LONG).show()
                        }
                        12500 -> {
                            Log.e(TAG, "Access/Authorization Error API", e)
                            Toast.makeText(this, "Sign-in failed - ApiException - Access/Authorization Error API : ${e.message}", Toast.LENGTH_LONG).show()
                        }
                        12501 -> {
                            Log.e(TAG, "Access Blocked API\nhttps://console.cloud.google.com/apis/api/photoslibrary.googleapis.com", e)
                            Toast.makeText(this, "Sign-in failed - ApiException - Access Blocked API : ${e.message}", Toast.LENGTH_LONG).show()
                        }
                        12502 -> {
                            Log.e(TAG, "An Other API Authentication Already Running", e)
                            Toast.makeText(this, "Sign-in failed - ApiException - An Other API Authentication Already Running : ${e.message}", Toast.LENGTH_LONG).show()
                        } else -> {
                            Log.e(TAG, "Sign-in failed - Unknown Code: ${e.message}", e)
                            Toast.makeText(this, "Sign-in failed - ApiException - Unknown Code:$code : ${e.message}", Toast.LENGTH_LONG).show()
                        }

                    }
                }
                else -> {
                    Log.e(TAG, "Sign-in failed: ${e.message}", e)
                    Toast.makeText(this, "Sign-in failed: ${e.message}", Toast.LENGTH_LONG).show()
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

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PhotoManagerTheme {
        Greeting("Android")
    }
}