package com.timtam.thejunktion.view.home

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Announcement
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.timtam.thejunktion.composables.JunktionTopAppBar
import com.timtam.thejunktion.ui.theme.BlueFilkom
import com.timtam.thejunktion.ui.theme.OrangeFilkom
import com.timtam.thejunktion.view.home.tabs.AnnouncementTab
import com.timtam.thejunktion.view.home.tabs.PostsTab
import com.timtam.thejunktion.view.home.tabs.ProfileTab
import com.timtam.thejunktion.viewmodel.AuthViewModel
import com.timtam.thejunktion.viewmodel.UserViewModel
import kotlinx.coroutines.delay


sealed class BottomNavigationTabs(
    val route: String,
    val icon: ImageVector
){
    object PostsTab: BottomNavigationTabs("posts", Icons.Filled.Home)
    object AnnouncementTab: BottomNavigationTabs("announcement", Icons.Filled.Announcement)
    object ProfileTab: BottomNavigationTabs("profile", Icons.Filled.Person)
}

sealed class BackPress{
    object Idle: BackPress()
    object InitialTouch: BackPress()
}

@Composable
fun HomeScreen(
    goToLogin: () -> Unit,
    goToEditProfile: () -> Unit,
    goToNewPost: () -> Unit,
    goToPostDetail: (id: String, type: String) -> Unit,
    userViewModel: UserViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel(),
) {

    val navController = rememberNavController()
    val bottomNavigationItems = listOf(
        BottomNavigationTabs.PostsTab,
        BottomNavigationTabs.AnnouncementTab,
        BottomNavigationTabs.ProfileTab,
    )

    var isFabVisible by remember { mutableStateOf(true) }
    var isDoubleBackToastShown by remember { mutableStateOf(false) }
    var backPressState by remember { mutableStateOf<BackPress>(BackPress.Idle) }

    if (isDoubleBackToastShown){
        Toast.makeText(LocalContext.current, "Tekan sekali lagi untuk keluar",
            Toast.LENGTH_SHORT).show()
        isDoubleBackToastShown = false
    }

    LaunchedEffect(key1 = backPressState){
        if (backPressState == BackPress.InitialTouch){
            delay(1000)
            backPressState = BackPress.Idle
        }
    }

    BackHandler(backPressState == BackPress.Idle) {
        backPressState = BackPress.InitialTouch
        isDoubleBackToastShown = true
    }

    Scaffold(
        topBar = {
            JunktionTopAppBar(isBackable = false)
        },
        content = { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = BottomNavigationTabs.PostsTab.route){
                composable(BottomNavigationTabs.PostsTab.route){
                    PostsTab(innerPadding, goToPostDetail)
                }
                composable(BottomNavigationTabs.AnnouncementTab.route){
                    AnnouncementTab(innerPadding, goToPostDetail)
                }
                composable(BottomNavigationTabs.ProfileTab.route){
                    ProfileTab(
                        innerPadding = innerPadding,
                        goToLogin =  goToLogin,
                        goToEditProfile = goToEditProfile,
                        userViewModel, authViewModel
                    )
                }
            }
        },
        bottomBar = {
            HomeBottomNavigation(navController, bottomNavigationItems){isFabShown ->
                isFabVisible = isFabShown
            }
        },
        floatingActionButton = {
            if (isFabVisible){
                FloatingActionButton(
                    backgroundColor = BlueFilkom,
                    modifier = Modifier.padding(10.dp),
                    onClick = goToNewPost
                ) {
                    Icon(
                        imageVector = Icons.Filled.PostAdd,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }
    )
}

@Composable
private fun HomeBottomNavigation(
    navController: NavHostController,
    tabItems: List<BottomNavigationTabs>,
    isFabShown: (Boolean) -> Unit
) {
    BottomNavigation(
        backgroundColor = BlueFilkom,
    ){
        val currentRoute = currentRoute(navController)
        tabItems.forEach{ tab ->
            val isSelected = currentRoute == tab.route
            BottomNavigationItem(
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = null,
                        tint = if (isSelected) OrangeFilkom else Color.Black
                    )
                },
                selected = currentRoute == tab.route,
                onClick = {
                    if (currentRoute != tab.route){
                        if (tab.route == BottomNavigationTabs.ProfileTab.route){
                            isFabShown(false)
                        } else {
                            isFabShown(true)
                        }
                        navController.navigate(tab.route)
                    }
                }
            )
        }
    }
}

@Composable
private fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}