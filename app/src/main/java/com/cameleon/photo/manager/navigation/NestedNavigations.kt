package com.cameleon.photo.manager.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.cameleon.photo.manager.bean.PhotoSize
import com.cameleon.photo.manager.bean.extension.urlBySize
import com.cameleon.photo.manager.extension.formatRoute
import com.cameleon.photo.manager.ui.login.LoginScreen
import com.cameleon.photo.manager.view.page.photo.GooglePhotoItemScreen
import com.cameleon.photo.manager.view.page.photo.GooglePhotosScreen
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

/**
 * Login, registration, forgot password screens nav graph builder
 * (Unauthenticated user)
 */
fun NavGraphBuilder.unauthenticatedGraph(navController: NavController, onLoginClicked: () -> Unit = {}) {

    navigation (
        route = NavigationRoutes.Unauthenticated.NavigationRoute.route,
        startDestination = NavigationRoutes.Unauthenticated.LoginRoute.route
    ) {
        // Login
        composable(route = NavigationRoutes.Unauthenticated.LoginRoute.route) {
            LoginScreen(onLoginClicked = onLoginClicked)
        }
    }
}

/**
 * Authenticated screens nav graph builder
 */
fun NavGraphBuilder.authenticatedGraph(navController: NavController, onUnAuthenticate: () -> Unit) {
    navigation(
        route = NavigationRoutes.Authenticated.NavigationRoute.route,
        startDestination = NavigationRoutes.Authenticated.PhotoAllRoute.route
    ) {
        // User
        composable(route = NavigationRoutes.Authenticated.PhotoAllRoute.route) { backStackEntry ->
            GooglePhotosScreen(onUnAuthenticate = onUnAuthenticate) {
                val url = it
                    .let {
                        NavigationRoutes.Authenticated.PhotoRoute.route.formatRoute("url", value = it.urlBySize(PhotoSize.Full), urlEncode = true)
                    }
                navController.navigate(url)
            }
        }
        // User
        composable(route = NavigationRoutes.Authenticated.PhotoRoute.route) { backStackEntry ->
            backStackEntry.arguments?.getString("url")
                ?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) }
                ?.let { url ->
                    GooglePhotoItemScreen(url)
                }
        }
    }
}