package com.timtam.thejunktion.view.home.tabs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.timtam.thejunktion.composables.PostOrAnnouncementCard
import com.timtam.thejunktion.viewmodel.PostViewModel

@Composable
fun PostsTab(
    innerPadding: PaddingValues,
    goToPostDetail: (id: String, type: String) -> Unit,
    postViewModel: PostViewModel = viewModel()
) {

    var isLoading by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit){
        isLoading = true
        isError = false
        postViewModel.buildLikeDetails()
        postViewModel.buildPostOrAnnouncement(
            onCompleteCallback = { isLoading = false },
            onErrorCallback = { isError = true },
            type = "posts",
        )
    }

    Box(
        Modifier.padding(innerPadding)
    ){
        if (isLoading){
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(20.dp)
            ){
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else if (isError){
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(20.dp)
            ){
                Text(
                    text = "Terjadi kesalahan",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    vertical = 15.dp,
                    horizontal = 20.dp
                ),
            ) {
                items(postViewModel.allPostsList.value){ post ->
                    val isLikedByUser = postViewModel.allPostsLikedId.value.contains(post.id)
                    PostOrAnnouncementCard(
                        postAndAnnouncement = post,
                        isUpvotedOrLikedByUser = isLikedByUser,
                        onPostClick = { id, type -> goToPostDetail(id, type) },
                        onLikeClick = { id -> postViewModel.likePost(id, "posts") },
                        onDislikeClick = { id -> postViewModel.dislikePost(id, "posts") },
                    )
                }
            }
        }
    }
}

