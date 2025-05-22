package com.cameleon.photo.manager.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MainAppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    isSignedIn: Boolean,
    onUnAuthenticate: () -> Unit = {},
    onLoginClicked: () -> Unit = {},
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = "root"
    ) {

        composable("root") {
            // Redirige automatiquement selon isSignedIn
            LaunchedEffect(isSignedIn) {
                navController.navigate(
                    if (isSignedIn) NavigationRoutes.Authenticated.NavigationRoute.route
                    else NavigationRoutes.Unauthenticated.NavigationRoute.route
                ) {
                    popUpTo("root") { inclusive = true }
                }
            }
        }

        // Unauthenticated user flow screens
        unauthenticatedGraph(navController = navController, onLoginClicked = onLoginClicked)

        // Authenticated user flow screens
        authenticatedGraph(navController = navController, onUnAuthenticate = onUnAuthenticate)
    }
}

