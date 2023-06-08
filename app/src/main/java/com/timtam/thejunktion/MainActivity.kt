package com.timtam.thejunktion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.timtam.thejunktion.ui.theme.TheJunktionTheme
import com.timtam.thejunktion.view.SplashScreen
import com.timtam.thejunktion.view.auth.LoginScreen
import com.timtam.thejunktion.view.auth.RegisterScreen
import com.timtam.thejunktion.view.home.EditProfileScreen
import com.timtam.thejunktion.view.home.HomeScreen
import com.timtam.thejunktion.view.post.NewPostScreen
import com.timtam.thejunktion.view.post.PostDetailScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TheJunktionTheme(false) {
                // A surface container using the 'background' color from the theme
                TheJunktionApp()
            }
        }
    }
}

@Composable
private fun TheJunktionApp() {
    val navController = rememberNavController()
    NavHost(
      navController = navController,
      startDestination = "splash") {
        composable(route = "splash"){
            SplashScreen(
                goToLogin = {
                    navController.navigate("login") {
                        popUpTo("splash") {inclusive = true}
                    }
                },
                goToHome = {
                    navController.navigate("home"){
                        popUpTo("splash") {inclusive = true}
                    }
                }
            )
        }
        composable(route = "login"){
            LoginScreen(
                onRegisterClick = { navController.navigate("register") },
                goToHome = {
                    navController.navigate("home"){
                        popUpTo("login") {inclusive = true}
                    }
                }
            )
        }
        composable(route = "register"){
            RegisterScreen(
                goToHome = {
                    navController.navigate("home"){
                        popUpTo("register") {inclusive = true}
                    }
                },
                onLoginClick = { navController.popBackStack() },
            )
        }
        composable(route = "home"){
            HomeScreen(
                goToLogin = {
                    navController.navigate("login"){
                        popUpTo("home") {inclusive = true}
                    }
                },
                goToEditProfile = { navController.navigate("edit-profile") },
                goToNewPost = { navController.navigate("new-post") },
                goToPostDetail = {id, type -> navController.navigate("post-detail/$id/$type") }
            )
        }
        composable(route = "edit-profile"){
            EditProfileScreen(
                goToHome = { navController.popBackStack() }
            )
        }
        composable(route = "new-post"){
            NewPostScreen(
                goToHome = { navController.popBackStack() }
            )
        }
        composable(
            route = "post-detail/{post_id}/{type}",
            arguments = listOf(
                navArgument("post_id"){ NavType.StringType },
                navArgument("type"){ NavType.StringType },
            ),
        ){
            val postId = it.arguments?.getString("post_id")
            val postType = it.arguments?.getString("type")
            PostDetailScreen(
                id = postId ?: "",
                type = postType ?: "",
                goToHome = { navController.popBackStack() }
            )
        }
    }
}

