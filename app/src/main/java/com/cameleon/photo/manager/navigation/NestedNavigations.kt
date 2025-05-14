package com.cameleon.photo.manager.navigation

import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
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
fun NavGraphBuilder.unauthenticatedGraph(navController: NavController, lifecycleOwner: LifecycleOwner) {

    navigation (
        route = NavigationRoutes.Unauthenticated.NavigationRoute.route,
        startDestination = NavigationRoutes.Unauthenticated.LoginRoute.route
    ) {
        // Login
        composable(route = NavigationRoutes.Unauthenticated.LoginRoute.route) {

            val onLogin = {
                navController.navigate(route = NavigationRoutes.Authenticated.PhotoAllRoute.route)
            }

            LoginScreen(onLogin = onLogin)
        }
    }
}

/**
 * Authenticated screens nav graph builder
 */
fun NavGraphBuilder.authenticatedGraph(navController: NavController) {
    navigation(
        route = NavigationRoutes.Authenticated.NavigationRoute.route,
        startDestination = NavigationRoutes.Authenticated.PhotoAllRoute.route
    ) {
        // User
        composable(route = NavigationRoutes.Authenticated.PhotoAllRoute.route) { backStackEntry ->
            GooglePhotosScreen {
                val url = it
                    .let {
                        NavigationRoutes.Authenticated.PhotoRoute.route.formatRoute("url", value = it, urlEncode = true)
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