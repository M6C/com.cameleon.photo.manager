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
            println("-----------------------> Navigate to -> LoginRoute")

            val onLogin = {
                println("-----------------------> onLogin Navigate to -> LoginScreen")
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
    navigation (
        route = NavigationRoutes.Authenticated.NavigationRoute.route,
        startDestination = NavigationRoutes.Authenticated.PhotoAllRoute.route
    ) {
        // Photo List
        composable(route = NavigationRoutes.Authenticated.PhotoAllRoute.route) { backStackEntry ->
            println("-----------------------> Navigate to -> PhotoAllRoute")
            val onClickItem = { url: String -> navController.navigate(NavigationRoutes.Authenticated.PhotoRoute.route.formatRoute("url", url)) }
            GooglePhotosScreen(onClickItem = onClickItem)
        }
        // Photo
        composable(route = NavigationRoutes.Authenticated.PhotoRoute.route) { backStackEntry ->
            println("-----------------------> Navigate to -> PhotoRoute")
            backStackEntry.arguments?.getString("url")
//                ?.run { toInt() }
                ?.let { url ->
                    GooglePhotoItemScreen(url)
                }
        }
    }
}