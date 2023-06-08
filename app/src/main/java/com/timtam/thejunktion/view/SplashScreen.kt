package com.timtam.thejunktion.view

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.timtam.thejunktion.R
import com.timtam.thejunktion.viewmodel.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    goToLogin: () -> Unit,
    goToHome: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
){
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color(0xFFFFF7E9))
    ) {
        Image(
            painter = painterResource(R.drawable.the_junktion),
            contentDescription = null,
            modifier = Modifier.requiredHeight(110.dp)
        )
    }

    LaunchedEffect(Unit){
        delay(1000)
        val isUserLoggedIn = authViewModel.isLoggedIn()
        if (isUserLoggedIn) goToHome() else goToLogin()
    }
}