package com.timtam.thejunktion.view.post

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.TextFieldDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.timtam.thejunktion.composables.CommentChatter
import com.timtam.thejunktion.composables.JunktionTopAppBar
import com.timtam.thejunktion.composables.PostOrAnnouncementCard
import com.timtam.thejunktion.model.Comment
import com.timtam.thejunktion.ui.theme.OrangeFilkom
import com.timtam.thejunktion.viewmodel.PostViewModel
import com.timtam.thejunktion.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun PostDetailScreen(
    id: String,
    type: String,
    goToHome: () -> Unit,
    postViewModel: PostViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel()
) {

    println(id)
    println(type)

    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }
    var isCommenting by remember { mutableStateOf(false) }

    var commentInput by rememberSaveable { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit){
        println("START EFEK")
        isLoading = true
        isError = false
        postViewModel.buildLikeDetails()
        postViewModel.buildDetailById(
            onCompleteCallback = {
                isLoading = false
            },
            onErrorCallback = { isError = true },
            id = id,
            type = type,
        )
    }

    println(postViewModel.allPostsLikedId.value.toString())

    Scaffold(
        topBar = {
            JunktionTopAppBar(
                isBackable = true,
                onBackPressed = goToHome,
            )
        },
    ){ innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ){
            if (isLoading){
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (isError){
                Text(
                    text = "Terjadi kesalahan",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxHeight()
                ){
                    LazyColumn(
                        contentPadding = innerPadding,
                        modifier = Modifier
                            .padding(top = 20.dp)
                            .padding(bottom = 0.dp)
                            .padding(horizontal = 10.dp)
                            .fillMaxWidth()
                            .weight(1F, false)
                    ) {
                        val listOfAllComment = postViewModel.allCommentOfPost.value
                        item {
                            val isLikedByUser = postViewModel.allPostsLikedId.value.contains(id)
                            PostOrAnnouncementCard(
                                postAndAnnouncement = postViewModel.detailsById.value!!,
                                isUpvotedOrLikedByUser = isLikedByUser,
                                onLikeClick = { id -> postViewModel.likePost(id, type, false) },
                                onDislikeClick = { id -> postViewModel.dislikePost(id, type, false) },
                                isOnDetail = true,
                                isAnnouncement = type == "announcements"
                            )
                        }
                        item {
                            Spacer(Modifier.requiredHeight(20.dp))
                        }
                        item {
                            if (listOfAllComment.isEmpty()) {
                                Text(
                                    text = "Belum ada komentar",
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            } else {
                                Text(
                                    text = "Semua komentar: ${postViewModel.allCommentOfPost.value.size}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(start = 10.dp)
                                )
                            }
                        }
                        if (listOfAllComment.isNotEmpty()) {
                            items(postViewModel.allCommentOfPost.value) { comment ->
                                CommentBox(comment = comment)
                            }
                        }
                        item {
                            Spacer(Modifier.requiredHeight(20.dp))
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFAB77))
                            .padding(5.dp)
                    ){
                        CommentChatter(
                            value = commentInput,
                            onValueChange = { commentInput = it },
                            shape = RoundedCornerShape(10.dp),
                            textStyle = MaterialTheme.typography.bodyMedium,
                            singleLine = true,
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = OrangeFilkom,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                            ),
                            placeholder = {
                                Text(
                                    text = "Tulis komentarmu...",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White
                                )
                            },
                            modifier = Modifier.weight(1F),
                            contentPadding = PaddingValues(10.dp),
                        )
                        IconButton(
                            enabled = !isCommenting && commentInput.isNotEmpty(),
                            onClick = {
                                isCommenting = true
                                coroutineScope.launch {
                                    postViewModel.addComment(
                                        type = type,
                                        id = id,
                                        content = commentInput,
                                        name = userViewModel.userData.value!!.name,
                                        onCompleteCallback = {
                                            isCommenting = false
                                            commentInput = ""
                                        },
                                        onErrorCallback = { isCommenting = false }
                                    )
                                }
                            }
                        ) {
                            if (isCommenting){
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier
                                        .requiredHeight(20.dp)
                                        .requiredWidth(20.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.Send,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CommentBox(
    comment: Comment
){
    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ){
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = null
        )
        Spacer(Modifier.requiredWidth(20.dp))
        Column {
            Text(
                text = comment.sender,
                style = MaterialTheme.typography.titleSmall,
            )
            Spacer(Modifier.requiredHeight(5.dp))
            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(Modifier.requiredHeight(5.dp))
            Text(
                text = comment.timestamp.toString(),
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}