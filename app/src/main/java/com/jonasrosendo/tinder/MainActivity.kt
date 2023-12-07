package com.jonasrosendo.tinder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jonasrosendo.tinder.navigation.RouteNavigation
import com.jonasrosendo.tinder.ui.features.login.LoginScreen
import com.jonasrosendo.tinder.ui.features.profile.ProfileScreen
import com.jonasrosendo.tinder.ui.features.signup.SignupScreen
import com.jonasrosendo.tinder.ui.features.splash.SplashScreen
import com.jonasrosendo.tinder.ui.screens.ChatListScreen
import com.jonasrosendo.tinder.ui.screens.SingleChatScreen
import com.jonasrosendo.tinder.ui.features.swipe.SwipeCards
import com.jonasrosendo.tinder.ui.theme.TinderTheme
import com.jonasrosendo.tinder.utils.NotificationMessage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TinderTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    App()
                }
            }
        }
    }
}

@Composable
fun App() {
    val navController: NavHostController = rememberNavController()
    val viewModel = hiltViewModel<TinderViewModel>()
    val currentUser = Firebase.auth.currentUser

    NotificationMessage(viewModel = viewModel)

    NavigationGraph(navController = navController, viewModel = viewModel, currentUser = currentUser)
}

@Composable
fun NavigationGraph(
    navController: NavHostController,
    viewModel: TinderViewModel,
    currentUser: FirebaseUser?
) {
    val startRoute =
        if (currentUser != null)
            RouteNavigation.Swipe.route
        else
            RouteNavigation.Login.route

    NavHost(navController = navController, startDestination = startRoute) {
        composable(RouteNavigation.Splash.route) {
            SplashScreen(viewModel = viewModel, navController = navController)
        }
        composable(RouteNavigation.Signup.route) {
            SignupScreen(navController)
        }
        composable(RouteNavigation.Swipe.route) {
            SwipeCards(navController)
        }
        composable(RouteNavigation.Login.route) {
            LoginScreen(navController)
        }
        composable(RouteNavigation.ChatList.route) {
            ChatListScreen(navController)
        }
        composable(RouteNavigation.Profile.route) {
            ProfileScreen(navController)
        }
        composable(RouteNavigation.SingleChat.route) {
            SingleChatScreen(chatId = "123", navController = navController)
        }
    }
}