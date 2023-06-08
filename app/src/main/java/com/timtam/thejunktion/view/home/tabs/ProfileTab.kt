package com.timtam.thejunktion.view.home.tabs

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.timtam.thejunktion.R
import com.timtam.thejunktion.ui.theme.DarkBlueFilkom
import com.timtam.thejunktion.ui.theme.LightBlueFilkom
import com.timtam.thejunktion.viewmodel.AuthViewModel
import com.timtam.thejunktion.viewmodel.UserViewModel

@Composable
fun ProfileTab(
    innerPadding: PaddingValues,
    goToLogin: () -> Unit,
    goToEditProfile: () -> Unit,
    userViewModel: UserViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
){

    var isLoading by remember { mutableStateOf(false) }
    val userData = userViewModel.userData.value

    isLoading = userData == null

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(30.dp)
            .padding(innerPadding)
            .fillMaxWidth()
    ) {
        if (isLoading){
            CircularProgressIndicator()
        } else {
            val builtUserData = userData!!
            Image(
                painter = painterResource(R.drawable.default_placeholder),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(150.dp)
                    .border(3.dp, DarkBlueFilkom, CircleShape)
            )
            TextButton(onClick = goToEditProfile) {
                Icon(Icons.Outlined.Edit, null)
                Text("Edit Profil")
            }
            Spacer(modifier = Modifier.requiredHeight(20.dp))
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Nama Lengkap",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = builtUserData.name.ifEmpty { "Belum diisi" },
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.requiredHeight(20.dp))
                Text(
                    text = "Alamat Email",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = builtUserData.email.ifEmpty { "Belum diisi" },
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.requiredHeight(20.dp))
                Text(
                    text = "NIM",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = builtUserData.nim.ifEmpty { "Belum diisi" },
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.requiredHeight(20.dp))
                Text(
                    text = "Program Studi",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = builtUserData.prodi.ifEmpty { "Belum diisi" },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.requiredHeight(50.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(LightBlueFilkom),
                elevation = ButtonDefaults.buttonElevation(2.dp),
                onClick = { authViewModel.signOut(goToLogin) },
            ) {
                Icon(Icons.Outlined.Logout, null)
                Spacer(modifier = Modifier.requiredWidth(10.dp))
                Text(
                    text = "Keluar",
                    modifier = Modifier.weight(1F)
                )
                Spacer(modifier = Modifier.requiredWidth(10.dp))
                Icon(Icons.Outlined.ArrowForwardIos, null)
            }
        }

    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun ShowPreview() {
    ProfileTab(PaddingValues(10.dp), {}, {})
}