package com.timtam.thejunktion.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.timtam.thejunktion.R
import com.timtam.thejunktion.ui.theme.DarkBlueFilkom

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JunktionTopAppBar(
    isLoading: Boolean = false,
    onBackPressed: () -> Unit = {},
    isBackable: Boolean,
){
    TopAppBar(
        navigationIcon = {
            if (isBackable){
                IconButton(
                    onClick = {
                        if (!isLoading){
                            onBackPressed()
                        }
                    },
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIos,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        },
        title = {
            Image(
                painter = painterResource(R.drawable.logo_font_only),
                contentDescription = null,
                modifier = Modifier.requiredWidth(200.dp)
            )
        },
        actions = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(DarkBlueFilkom)
    )
}