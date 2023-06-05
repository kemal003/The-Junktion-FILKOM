package com.timtam.thejunktion.view.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.timtam.thejunktion.ui.theme.BlueFilkom
import com.timtam.thejunktion.ui.theme.LightBlueFilkom
import com.timtam.thejunktion.viewmodel.AuthViewModel


@Composable
fun RegisterScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    authViewModel: AuthViewModel,
) {

    var nameInput by rememberSaveable { mutableStateOf("") }
    var emailInput by rememberSaveable { mutableStateOf("") }
    var passInput by rememberSaveable { mutableStateOf("") }
    var confirmPassInput by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(color = Color(0xFFFFF7E9))
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
            onTextChange = { passInput = it }
        )
        Spacer(modifier = Modifier.requiredHeight(20.dp))
        AuthFieldForm(
            inputValue = confirmPassInput,
            icon = Icons.Outlined.Lock,
            label = "Konfirmasi Password",
            isPassword = true,
            onTextChange = { confirmPassInput = it }
        )
        Spacer(modifier = Modifier.requiredHeight(50.dp))
        Button(
            onClick = {
                authViewModel.signUp(
                    email = emailInput,
                    password = passInput,
                    name = nameInput,
                    registerCallBack = onRegisterClick
                )
            },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = LightBlueFilkom,
            ),
        ){
            Text(
                text = "Register",
                modifier = Modifier.padding(horizontal = 10.dp)
            )
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
                onClick = {
                }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AuthFieldForm(
    inputValue: String,
    icon: ImageVector,
    label: String,
    isPassword: Boolean = false,
    onTextChange: (String) -> Unit
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
        textStyle = MaterialTheme.typography.bodyMedium,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Ascii
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                Color(0xFF909090),
                RoundedCornerShape(10.dp),
            ),
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ),
    )
}
//
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//private fun RegisterPreview() {
//    RegisterScreen(
//        onLoginClick = {},
//        onRegisterClick = {}
//    )
//}