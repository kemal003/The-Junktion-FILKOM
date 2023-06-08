package com.timtam.thejunktion.view.auth

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.timtam.thejunktion.ui.theme.BlueFilkom
import com.timtam.thejunktion.ui.theme.LightBlueFilkom
import com.timtam.thejunktion.viewmodel.AuthViewModel
import kotlinx.coroutines.launch


@Composable
fun RegisterScreen(
    onLoginClick: () -> Unit,
    goToHome: () -> Unit,
) {
    val authViewModel: AuthViewModel = viewModel()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val snackbarScope = rememberCoroutineScope()

    var nameInput by rememberSaveable { mutableStateOf("") }
    var emailInput by rememberSaveable { mutableStateOf("") }
    var passInput by rememberSaveable { mutableStateOf("") }
    var confirmPassInput by rememberSaveable { mutableStateOf("") }

    var passwordVisibility by remember { mutableStateOf(false) }
    var isError by rememberSaveable { mutableStateOf(false) }
    var supportMessage by rememberSaveable { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }

    fun validate(password: String, confirmPassword: String){
        if (password.length < 8 || confirmPassword.length < 8){
            isError = true
            supportMessage = "Password harus lebih dari 8 karakter"
        } else if (password != confirmPassword){
            isError = true
            supportMessage = "Password tidak sama"
        } else {
            isError = false
            supportMessage = ""
        }
    }

    fun isButtonEnabled(): Boolean{
        return !isError && emailInput.isNotEmpty() && passInput.isNotEmpty()
                && nameInput.isNotEmpty() && confirmPassInput.isNotEmpty()
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

    BackHandler(isLoading) { }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) {innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .background(color = Color(0xFFFFF7E9))
                .padding(innerPadding)
                .padding(all = 50.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Row (
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){
                IconButton(onClick = onLoginClick) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = null,
                        tint = BlueFilkom,
                    )
                }
                Text(
                    text = "Buat Akun",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Left,
                )
            }
            Spacer(modifier = Modifier.requiredHeight(20.dp))
            AuthFieldForm(
                inputValue = nameInput,
                icon = Icons.Outlined.Person,
                label = "Nama Lengkap",
                onTextChange = { nameInput = it }
            )
            Spacer(modifier = Modifier.requiredHeight(20.dp))
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
                    validate(passInput, confirmPassInput)
                },
                onPasswordVisibilityChange = { passwordVisibility = !passwordVisibility },
                isPasswordVisible = passwordVisibility,
            )
            Spacer(modifier = Modifier.requiredHeight(20.dp))
            AuthFieldForm(
                inputValue = confirmPassInput,
                icon = Icons.Outlined.Lock,
                label = "Konfirmasi Password",
                isPassword = true,
                onTextChange = {
                    confirmPassInput = it
                    validate(passInput, confirmPassInput)
                },
                onPasswordVisibilityChange = { passwordVisibility = !passwordVisibility },
                isPasswordVisible = passwordVisibility,
                isError = isError,
                supportText = supportMessage,
            )
            Spacer(modifier = Modifier.requiredHeight(50.dp))
            if (isLoading){
                CircularProgressIndicator()
            } else {
                Button(
                    enabled = isButtonEnabled(),
                    onClick = {
                        isLoading = true
                        coroutineScope.launch{
                            authViewModel.signUp(
                                email = emailInput,
                                password = passInput,
                                name = nameInput,
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
                    colors = ButtonDefaults.buttonColors(LightBlueFilkom),
                    elevation = ButtonDefaults.buttonElevation(2.dp),
                    ){
                    Text(
                        text = "Register",
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.requiredHeight(20.dp))
            Row (
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = "Sudah memiliki akun?",
                    style = MaterialTheme.typography.bodySmall,
                )
                TextButton(
                    onClick = onLoginClick
                ) {
                    Text(
                        text = "Masuk",
                        color = LightBlueFilkom,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}

@Composable
private fun AuthFieldForm(
    inputValue: String,
    icon: ImageVector,
    label: String,
    isPassword: Boolean = false,
    onTextChange: (String) -> Unit,
    onPasswordVisibilityChange: () -> Unit = {},
    isPasswordVisible: Boolean = false,
    isError: Boolean = false,
    supportText: String = "",
){
    TextField(
        value = inputValue,
        onValueChange = onTextChange,
        shape = RoundedCornerShape(10.dp),
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = BlueFilkom,
            )
        },
        label = {
            Text(
                text = label,
                color = Color(0xFF979797),
                style = MaterialTheme.typography.labelMedium,
            )
        },
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyMedium,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Ascii
        ),
        visualTransformation = if (isPassword) {
            if (!isPasswordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            }
        } else {
            VisualTransformation.None
        },
        isError = isError,
        supportingText = {
            if (isError){
                Text(
                    text = supportText,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Red,
                )
            }
        },
        trailingIcon = {
            if (isPassword){
                IconButton(
                    onClick = onPasswordVisibilityChange
                ) {
                    if (isPasswordVisible){
                        Icon(
                            imageVector = Icons.Filled.VisibilityOff,
                            contentDescription = null,
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Visibility,
                            contentDescription = null,
                        )
                    }
                }
            }
        },
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ),
    )
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun ShowPreview() {
    RegisterScreen({}, {})
}