package com.jonasrosendo.tinder.navigation

import androidx.navigation.NavController

sealed class RouteNavigation(val route: String) {

    object Splash: RouteNavigation("splash")
    object Signup : RouteNavigation("signup")
    object Login : RouteNavigation("login")
    object Profile : RouteNavigation("profile")
    object Swipe : RouteNavigation("swipe")
    object ChatList : RouteNavigation("chatList")
    object SingleChat : RouteNavigation("singleChat/{chatId}") {
        fun createRoute(id: String) = "singleChat/$id"
    }
}

fun NavController.navigateTo(route: String) {
    navigate(route) {
        popUpTo(route)
        launchSingleTop = true
    }
}

fun NavController.navigateToAndClearStack(route: String) {
    navigate(route) {
        popUpTo(0)
        launchSingleTop = true
    }
}