package com.timtam.thejunktion.view.home.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.timtam.thejunktion.composables.PostOrAnnouncementCard
import com.timtam.thejunktion.ui.theme.DarkBlueFilkom
import com.timtam.thejunktion.ui.theme.GrayBackground
import com.timtam.thejunktion.viewmodel.PostViewModel
import kotlinx.coroutines.launch

@Composable
fun AnnouncementTab(
    innerPadding: PaddingValues,
    goToPostDetail: (id: String, type: String) -> Unit,
    postViewModel: PostViewModel = viewModel()
) {

    var isLoading by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var isInit by remember { mutableStateOf(true) }

    val orderType = arrayOf("likes", "timestamp")
    var orderBy by remember { mutableStateOf(orderType[0]) }

    val coroutineScope = rememberCoroutineScope()

    fun loadDataWithOrderBy(){
        coroutineScope.launch {
            isLoading = true
            isError = false
            postViewModel.buildLikeDetails()
            postViewModel.buildPostOrAnnouncement(
                onCompleteCallback = { isLoading = false },
                onErrorCallback = { isError = true },
                type = "announcements",
                orderBy = orderBy,
            )
        }
    }

    LaunchedEffect(Unit){
        loadDataWithOrderBy()
        isInit = false
    }

    Box(
        Modifier.padding(innerPadding)
    ){
        if (isError){
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
                if (!isInit) {
                    item {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .background(GrayBackground, RoundedCornerShape(10.dp))
                                .clip(RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp)
                                .padding(vertical = 3.dp)
                        ) {
                            Button(
                                onClick = {
                                    orderBy = orderType[0]
                                    loadDataWithOrderBy()
                                },
                                enabled = orderBy != orderType[0],
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GrayBackground,
                                    contentColor = Color.Gray,
                                    disabledContainerColor = DarkBlueFilkom,
                                    disabledContentColor = Color.White,
                                ),
                                modifier = Modifier.weight(1F),
                                shape = RoundedCornerShape(10.dp),
                                ){
                                Text("Upvote", style = MaterialTheme.typography.labelLarge)
                            }
                            Button(
                                onClick = {
                                    orderBy = orderType[1]
                                    loadDataWithOrderBy()
                                },
                                enabled = orderBy != orderType[1],
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GrayBackground,
                                    contentColor = Color.Gray,
                                    disabledContainerColor = DarkBlueFilkom,
                                    disabledContentColor = Color.White,
                                ),
                                modifier = Modifier.weight(1F),
                                shape = RoundedCornerShape(10.dp),
                                ){
                                Text("Terbaru", style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }
                }
                if (isLoading){
                    item {
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
                    }
                } else {
                    items(postViewModel.allPostsList.value){ post ->
                        val isLikedByUser = postViewModel.allPostsLikedId.value.contains(post.id)
                        PostOrAnnouncementCard(
                            postAndAnnouncement = post,
                            isUpvotedOrLikedByUser = isLikedByUser,
                            onPostClick = { id, type -> goToPostDetail(id, type) },
                            onLikeClick = { id -> postViewModel.likePost(id, "announcements") },
                            onDislikeClick = { id -> postViewModel.dislikePost(id, "announcements") },
                            isAnnouncement = true,
                        )
                    }
                }
            }
        }
    }
}