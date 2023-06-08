package com.timtam.thejunktion.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.timtam.thejunktion.ui.theme.FieldBackground
import com.timtam.thejunktion.ui.theme.GrayBackground

@Composable
fun JunktionTestField(
    title: String,
    inputValue: String,
    onTextChange: (String) -> Unit,
    placeholder: String,
    isSingleLine: Boolean = true,
    isReadOnly: Boolean = false,
){
    Text(
        text = title,
        modifier = Modifier.padding(start = 10.dp),
        style = MaterialTheme.typography.bodyMedium
    )
    TextField(
        value = inputValue,
        readOnly = isReadOnly,
        onValueChange = onTextChange,
        textStyle = MaterialTheme.typography.bodyMedium,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(FieldBackground)
            .border(
                shape = RoundedCornerShape(10.dp),
                color = GrayBackground,
                width = 2.dp
            ),
        singleLine = isSingleLine,
        minLines = if (isSingleLine) 1 else 6,
        maxLines = if (isSingleLine) 1 else 6,
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.labelMedium
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = FieldBackground,
            unfocusedContainerColor = FieldBackground,
            disabledContainerColor = FieldBackground,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            disabledTextColor = Color.Gray,
        )
    )
}