package com.timtam.thejunktion.view.post

import android.Manifest
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.Camera
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.timtam.thejunktion.R
import com.timtam.thejunktion.composables.JunktionTestField
import com.timtam.thejunktion.composables.JunktionTopAppBar
import com.timtam.thejunktion.ui.theme.CreamFilkom
import com.timtam.thejunktion.ui.theme.DarkBlueFilkom
import com.timtam.thejunktion.ui.theme.FieldBackground
import com.timtam.thejunktion.ui.theme.LightBlueFilkom
import com.timtam.thejunktion.utils.CompressionUtil
import com.timtam.thejunktion.viewmodel.PostViewModel
import com.timtam.thejunktion.viewmodel.UserViewModel
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class PhotoFileProvider: FileProvider(
    R.xml.photo_filepath
){
    companion object {
        suspend fun getPhotoUri(context: Context): Uri {
            val dir = File(context.cacheDir, "images")
            dir.mkdirs()

            val file = withContext(Dispatchers.IO) {
                return@withContext File.createTempFile(
                    "photos_taken_",
                    ".jpg",
                    dir
                )
            }

            val authority = context.packageName + ".PhotoFileProvider"

            return getUriForFile(context, authority, file)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun NewPostScreen(
    goToHome: () -> Unit,
    userViewModel: UserViewModel = viewModel(),
) {
    val postViewModel: PostViewModel = viewModel()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val snackbarScope = rememberCoroutineScope()

    val types = arrayOf("General", "Pengumuman")
    var isDropdownExpanded by remember { mutableStateOf(false) }

    var selectedType by rememberSaveable { mutableStateOf(types[0]) }
    var titleInput by rememberSaveable { mutableStateOf("") }
    var descriptionInput by rememberSaveable { mutableStateOf("") }
    var contactInput by rememberSaveable { mutableStateOf("") }
    val isAnonymous = remember { mutableStateOf(false) }

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    var isImageTaken by rememberSaveable { mutableStateOf(false) }
    var photoUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var compressedPhotoUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    var isLoading by remember { mutableStateOf(false) }

    fun showErrorSnackbar(errorMessage: String){
        snackbarScope.launch {
            if (postViewModel.isSnackbarShown){
                snackbarHostState.showSnackbar(
                    message = errorMessage,
                    duration = SnackbarDuration.Short,
                )
            }
        }
    }

    fun isValid(): Boolean{
        if (titleInput.isNotEmpty() || isImageTaken){
            if (selectedType == types[1]){
                return contactInput.isNotEmpty()
            }
        }
        return titleInput.isNotEmpty() || isImageTaken
    }

    val context = LocalContext.current

    suspend fun compressPhoto(context: Context) {
        println("COMPRESSING PHOTO")
        val photoNewUri = CompressionUtil.getFilePathFromUri(context, photoUri!!)
        withContext(Dispatchers.IO) {
            val photoFileWithNewUri = File(photoNewUri?.path!!)
            val compressedImageFile = Compressor.compress(context, photoFileWithNewUri)
            compressedPhotoUri = Uri.fromFile(compressedImageFile)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { isSuccess ->
            if (isSuccess){
                coroutineScope.launch (Dispatchers.IO){
                    compressPhoto(context)
                }
                isImageTaken = true
            }
        }
    )

    fun takePhoto(context: Context){
        println("TAKING PHOTO")
        coroutineScope.launch{
            val uri = PhotoFileProvider.getPhotoUri(context)
            photoUri = uri
            println("photoUri: $photoUri")
            cameraLauncher.launch(uri)
        }
    }

    BackHandler(isLoading) {
        Toast.makeText(context, "Mohon tunggu sebentar", Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            JunktionTopAppBar(
                isBackable = true,
                isLoading = isLoading,
                onBackPressed = goToHome,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(horizontal = 30.dp)
                .padding(innerPadding)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ){
            Spacer(Modifier.requiredHeight(20.dp))
            Text(
                text = "Tipe Postingan",
                modifier = Modifier.padding(start = 10.dp),
                style = MaterialTheme.typography.bodyMedium
            )
            ExposedDropdownMenuBox(
                expanded = isDropdownExpanded,
                onExpandedChange = { isDropdownExpanded = !isDropdownExpanded },
            ) {
                TextField(
                    value = selectedType,
                    onValueChange = { selectedType = it },
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = isDropdownExpanded
                        )
                    },
                    textStyle = MaterialTheme.typography.bodyMedium,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            shape = RoundedCornerShape(10.dp),
                            color = DarkBlueFilkom,
                            width = 2.dp
                        )
                        .menuAnchor()
                    ,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = FieldBackground,
                        unfocusedContainerColor = FieldBackground,
                        disabledContainerColor = FieldBackground,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    )
                )
                ExposedDropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false },
                    modifier = Modifier.background(CreamFilkom)
                ) {
                    types.forEach {type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                selectedType = type
                                isDropdownExpanded = false
                            },
                        )
                    }
                }
            }
            Spacer(Modifier.requiredHeight(20.dp))
            JunktionTestField(
                title = "Judul",
                inputValue = titleInput,
                onTextChange = { titleInput = it },
                placeholder = "Masukkan judul..."
            )
            Spacer(Modifier.requiredHeight(20.dp))
            JunktionTestField(
                title = "Deskripsi",
                inputValue = descriptionInput,
                onTextChange = { descriptionInput = it },
                placeholder = "Masukkan deskripsi...",
                isSingleLine = false,
            )
            Spacer(Modifier.requiredHeight(20.dp))
            Text(
                text = "Tambah foto",
                modifier = Modifier.padding(start = 10.dp),
                style = MaterialTheme.typography.bodyMedium
            )
            Button(
                onClick = {
                    if (cameraPermissionState.status.isGranted){
                        isImageTaken = false
                        takePhoto(context)
                    } else {
                        cameraPermissionState.launchPermissionRequest()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(LightBlueFilkom),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Camera,
                    contentDescription = null,
                )
                Spacer(Modifier.requiredWidth(10.dp))
                Text(
                    text = "Ambil Foto",
                )
            }
            Spacer(Modifier.requiredHeight(20.dp))
            if (isImageTaken && photoUri != null){
                AsyncImage(
                    model = photoUri,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(100.dp, 300.dp),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.image_placeholder),
                    error = painterResource(R.drawable.broken_image),
                )
            } else {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .requiredHeight(200.dp)
                        .background(FieldBackground)
                ){
                    Text(
                        text = "Belum ada foto terpilih",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            if (selectedType == types[1]){
                Spacer(Modifier.requiredHeight(20.dp))
                JunktionTestField(
                    title = "CP",
                    inputValue = contactInput,
                    onTextChange = { contactInput = it },
                    placeholder = "Masukkan CP..."
                )
            } else {
                contactInput = ""
                isAnonymous.value = false
                Spacer(Modifier.requiredHeight(20.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Checkbox(
                        checked = isAnonymous.value,
                        onCheckedChange = { isAnonymous.value = it },
                    )
                    Text(
                        text = "Sembunyikan nama",
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
            Spacer(Modifier.requiredHeight(50.dp))
            if (isLoading){
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(LightBlueFilkom),
                    elevation = ButtonDefaults.buttonElevation(2.dp),
                    enabled = isValid(),
                    onClick = {
                        val isPost = selectedType == types[0]
                        val path = if (isPost) "posts" else "announcements"
                        isLoading = true
                        coroutineScope.launch {
                            postViewModel.newPostOrAnnouncement(
                                path = path,
                                name = userViewModel.userData.value!!.name,
                                title = titleInput.ifEmpty { null },
                                description = descriptionInput.ifEmpty { null },
                                contact = contactInput.ifEmpty { null },
                                photo = compressedPhotoUri,
                                onErrorCallback = {
                                    showErrorSnackbar(it)
                                },
                                onCompleteCallback = {
                                    isLoading = false
                                    goToHome()
                                }
                            )
                        }
                    },
                ) {
                    Icon(Icons.Outlined.Logout, null)
                    Spacer(modifier = Modifier.requiredWidth(10.dp))
                    Text(
                        text = "Post",
                        modifier = Modifier.weight(1F)
                    )
                    Spacer(modifier = Modifier.requiredWidth(10.dp))
                    Icon(Icons.Outlined.ArrowForwardIos, null)
                }
            }
            Spacer(Modifier.requiredHeight(20.dp))
        }
    }
}