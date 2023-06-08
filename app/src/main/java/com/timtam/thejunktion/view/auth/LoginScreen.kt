package com.timtam.thejunktion.view.auth

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.timtam.thejunktion.R
import com.timtam.thejunktion.composables.AuthFieldForm
import com.timtam.thejunktion.ui.theme.LightBlueFilkom
import com.timtam.thejunktion.viewmodel.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


sealed class BackPress{
    object Idle: BackPress()
    object InitialTouch: BackPress()
}

@Composable
fun LoginScreen(
    onRegisterClick: () -> Unit,
    goToHome: () -> Unit,
){

    val authViewModel: AuthViewModel = viewModel()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val snackbarScope = rememberCoroutineScope()

    var emailInput by rememberSaveable { mutableStateOf("") }
    var passInput by rememberSaveable { mutableStateOf("") }

    var passwordVisibility by remember { mutableStateOf(false) }
    var isError by rememberSaveable { mutableStateOf(false) }
    var supportMessage by rememberSaveable { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var isDoubleBackToastShown by remember { mutableStateOf(false) }
    var backPressState by remember { mutableStateOf<BackPress>(BackPress.Idle) }

    fun validate(password: String){
        if (password.length < 8){
            isError = true
            supportMessage = "Password harus lebih dari 8 karakter"
        }  else {
            isError = false
            supportMessage = ""
        }
    }

    fun isButtonEnabled(): Boolean{
        return !isError && emailInput.isNotEmpty() && passInput.isNotEmpty()
    }

    fun showErrorSnackbar(errorMessage: String){
        snackbarScope.launch {
            if (authViewModel.isSnackbarShown){
                snackbarHostState.showSnackbar(
                    message = errorMessage,
                    duration = SnackbarDuration.Short,
                )
            }
        }
    }

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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .background(Color(0xFFFFF7E9))
                .padding(innerPadding)
                .padding(all = 50.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Image(
                painter = painterResource(R.drawable.the_junktion),
                contentDescription = null,
                modifier = Modifier.requiredHeight(height = 100.dp)
            )
            Spacer(modifier = Modifier.requiredHeight(50.dp))
            AuthFieldForm(
                inputValue = emailInput,
                icon = Icons.Outlined.Email,
                label = "Alamat Email",
                onTextChange = { emailInput = it }
            )
            Spacer(modifier = Modifier.requiredHeight(20.dp))
            AuthFieldForm(
                inputValue = passInput,
                icon = Icons.Outlined.Lock,
                label = "Password",
                isPassword = true,
                onTextChange = {
                    passInput = it
                    validate(passInput)
                },
                onPasswordVisibilityChange = { passwordVisibility = !passwordVisibility },
                isPasswordVisible = passwordVisibility,
                isError = isError,
                supportText = supportMessage,
            )
            Spacer(modifier = Modifier.requiredHeight(50.dp))
            if (isLoading){
                CircularProgressIndicator()
            }else {
                Button(
                    enabled = isButtonEnabled(),
                    onClick = {
                        isLoading = true
                        coroutineScope.launch{
                            authViewModel.signIn(
                                emailInput,
                                passInput,
                                onCompleteCallBack = {
                                    isLoading = false
                                    goToHome()
                                },
                                onErrorCallback = {
                                    isLoading = false
                                    showErrorSnackbar(it)
                                },
                            )
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LightBlueFilkom,
                    ),
                    elevation = ButtonDefaults.buttonElevation(2.dp),
                    ) {
                    Text(
                        text = "Login",
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.requiredHeight(20.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Belum memiliki akun?",
                    style = MaterialTheme.typography.bodySmall,
                )
                TextButton(
                    onClick = onRegisterClick,
                ) {
                    Text(
                        text = "Daftar",
                        color = LightBlueFilkom,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun ShowPreview() {
    LoginScreen({}, {})
}