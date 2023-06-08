package com.timtam.thejunktion.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.timtam.thejunktion.ui.theme.BlueFilkom

@Composable
fun AuthFieldForm(
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
        singleLine = true,
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
                style = MaterialTheme.typography.labelMedium,
            )
        },
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