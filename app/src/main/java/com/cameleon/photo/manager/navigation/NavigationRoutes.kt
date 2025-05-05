package com.cameleon.photo.manager.navigation

sealed class NavigationRoutes {

    // Unauthenticated Routes
    sealed class Unauthenticated(val route: String) : NavigationRoutes() {
        object NavigationRoute : Unauthenticated(route = "unauthenticated")
        object LoginRoute : Unauthenticated(route = "login")
    }

    // Authenticated Routes
    sealed class Authenticated(val route: String) : NavigationRoutes() {
        object NavigationRoute : Authenticated(route = "authenticated")
        object PhotoAllRoute : Authenticated(route = "photo")
        object PhotoRoute : Authenticated(route = "photo/{url}")
    }
}