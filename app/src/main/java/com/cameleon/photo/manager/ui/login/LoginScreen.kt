package com.cameleon.photo.manager.ui.login


import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cameleon.photo.manager.extension.getActivity
import com.cameleon.photo.manager.navigation.NavigationRoutes
import com.cameleon.photo.manager.ui.theme.PhotoManagerTheme
import com.cameleon.photo.manager.view.page.photo.PhotosViewModel

@Composable
fun LoginScreen(onLogin: () -> Unit = {}) {

    val context = LocalContext.current
    val activity = context.getActivity()

    val viewModel: PhotosViewModel = hiltViewModel()

    if (activity != null) {
        viewModel.isSignedIn.collectAsState().value.let {
            println("-----------------------> Navigate to -> GooglePhotosScreen isSignedIn:$it viewModel:$viewModel")
//            Toast.makeText(activity, "is Signed ${if (it) "in" else "out"}", Toast.LENGTH_SHORT).show()
            if (it) {
                onLogin()
//                val navController: NavHostController = rememberNavController()
//                navController.navigate(route = NavigationRoutes.Authenticated.PhotoAllRoute.route)
            }
        }

        LoginPage(onLoginClicked = {
            viewModel.launchSingIn(activity)
        })
    } else {
        ErrorMessage("Activity Not Initialized !")
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