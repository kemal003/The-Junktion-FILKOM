package com.timtam.thejunktion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.timtam.thejunktion.ui.theme.TheJunktionTheme
import com.timtam.thejunktion.view.auth.LoginScreen
import com.timtam.thejunktion.view.auth.RegisterScreen
import com.timtam.thejunktion.view.home.HomeScreen
import com.timtam.thejunktion.viewmodel.AuthViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TheJunktionTheme {
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
      startDestination = "login") {
        composable(route = "login"){
            LoginScreen{
                navController.navigate("register")
            }
        }
        composable(route = "register"){
            val viewModel = hiltViewModel<AuthViewModel>()
            RegisterScreen(
                onRegisterClick = {
                    navController.navigate("home")
                },
                onLoginClick = {
                    navController.navigate("login")
                },
                authViewModel = viewModel
            )
        }
        composable(route = "home"){
            HomeScreen()
        }
    }
}

