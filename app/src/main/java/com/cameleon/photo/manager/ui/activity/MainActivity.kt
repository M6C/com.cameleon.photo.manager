package com.cameleon.photo.manager.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.cameleon.photo.manager.navigation.MainAppNavHost
import com.cameleon.photo.manager.navigation.NavigationRoutes
import com.cameleon.photo.manager.ui.theme.PhotoManagerTheme
import com.cameleon.photo.manager.ui.topbar.TopAppBarComponent
import com.cameleon.photo.manager.view.page.photo.GooglePhotosViewModel
import com.cameleon.photo.manager.view.page.photo.PhotosViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        private val TAG = MainActivity::class.simpleName
    }

    private val viewModel: PhotosViewModel by viewModels()
    private val viewModelPhoto: GooglePhotosViewModel by viewModels()

    @Inject lateinit var googleSignInOptions: GoogleSignInOptions

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.singIn(this) {
            if (!viewModel.checkSignedIn()) {
                // Ensure clean state if not signed in
                viewModel.logOut()
                viewModelPhoto.logOut()
                viewModel.launchSingIn(this@MainActivity)
            }
        }

        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()
            val isSignedIn = viewModel.isSignedIn.collectAsState()
            val showLogoutDialog = remember { mutableStateOf(false) }

            if (showLogoutDialog.value) {
                AlertDialog(
                        onDismissRequest = { showLogoutDialog.value = false },
                        title = { Text("Déconnexion") },
                        text = {
                            Text(
                                    "Voulez-vous simplement vous déconnecter ou révoquer l'accès (ceci demandera à nouveau les permissions lors de la prochaine connexion) ?"
                            )
                        },
                        confirmButton = {
                            Button(
                                    onClick = {
                                        showLogoutDialog.value = false
                                        viewModel.revokeAccess()
                                        viewModelPhoto.logOut()
                                    }
                            ) { Text("Révoquer l'accès") }
                        },
                        dismissButton = {
                            TextButton(
                                    onClick = {
                                        showLogoutDialog.value = false
                                        viewModel.logOut()
                                        viewModelPhoto.logOut()
                                    }
                            ) { Text("Déconnexion simple") }
                        }
                )
            }

            viewModel.getUserMessage()?.let {
                Toast.makeText(applicationContext, "Message : $it", Toast.LENGTH_SHORT).show()
            }

            viewModel.getUserError()?.let {
                Toast.makeText(applicationContext, "Error : $it", Toast.LENGTH_SHORT).show()
            }

            PhotoManagerTheme {
                Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            TopAppBarComponent(isSignedIn = isSignedIn.value) {
                                // Logout action
                                if (isSignedIn.value) {
                                    showLogoutDialog.value = true
                                } else {
                                    viewModel.launchSingIn(this@MainActivity)
                                }
                            }
                        }
                ) { innerPadding ->
                    // Scrollable content area with sticky header support
                    Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                        MainAppNavHost(
                                navController = navController,
                                isSignedIn = isSignedIn.value,
                                onLoginClicked = { viewModel.launchSingIn(this@MainActivity) },
                                onUnAuthenticate = { showLogoutDialog.value = true }
                        )
                    }

                    // Trigger navigation when sign-in status changes
                    LaunchedEffect(isSignedIn.value) {
                        Log.d(TAG, "isSignedIn changed: ${isSignedIn.value}")
                        navController.navigate(
                                if (isSignedIn.value)
                                        NavigationRoutes.Authenticated.NavigationRoute.route
                                else NavigationRoutes.Unauthenticated.NavigationRoute.route
                        ) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            }
        }
    }
}
