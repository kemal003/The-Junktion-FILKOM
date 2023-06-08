package com.timtam.thejunktion.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.timtam.thejunktion.R
import com.timtam.thejunktion.model.PostAndAnnouncement
import com.timtam.thejunktion.ui.theme.CreamFilkom
import com.timtam.thejunktion.ui.theme.GrayBackground
import com.timtam.thejunktion.ui.theme.OrangeFilkom

@Composable
fun PostOrAnnouncementCard(
    postAndAnnouncement: PostAndAnnouncement,
    isUpvotedOrLikedByUser: Boolean = false,
    onPostClick: (id: String, type: String) -> Unit = { _, _ -> },
    onLikeClick: (id: String) -> Unit,
    onDislikeClick: (id: String) -> Unit,
    isAnnouncement: Boolean = false,
    isOnDetail: Boolean = false,
){
    var isUpvotedOrLiked by remember { mutableStateOf(isUpvotedOrLikedByUser) }
    val type = if (isAnnouncement) "announcements" else "posts"

    Card(
        colors = CardDefaults.cardColors(CreamFilkom),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .clickable { onPostClick(postAndAnnouncement.id, type) },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            Modifier
                .padding(top = 10.dp)
                .padding(horizontal = 10.dp)
        ) {
            Row {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.requiredWidth(10.dp))
                Column(
                    Modifier.weight(1F)
                ) {
                    Text(
                        text = postAndAnnouncement.sender,
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Text(
                        text = postAndAnnouncement.timestamp.toString(),
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
            Spacer(modifier = Modifier.requiredHeight(10.dp))
            if (postAndAnnouncement.title.isNotEmpty()){
                Text(
                    text = postAndAnnouncement.title,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
                Spacer(modifier = Modifier.requiredHeight(10.dp))
            }
            if (postAndAnnouncement.photoUrl.isNotEmpty()){
                Box(
                    Modifier.padding(horizontal = 10.dp)
                ) {
                    Box(
                        if (isOnDetail) Modifier
                            .fillMaxWidth()
                            .heightIn(150.dp)
                        else Modifier
                            .fillMaxWidth()
                            .background(GrayBackground)
                            .heightIn(0.dp, 200.dp)
                    ) {
                        AsyncImage(
                            model = postAndAnnouncement.photoUrl,
                            placeholder = painterResource(R.drawable.image_placeholder),
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .fillMaxHeight(),
                            contentScale = ContentScale.FillHeight,
                        )
                    }
                }
            }
            if (isAnnouncement){
                Spacer(Modifier.requiredHeight(10.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ){
                    Text(
                        text = "PENGUMUMAN",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "CP: ${postAndAnnouncement.contact}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
            ) {
                TextButton(
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.Black
                    ),
                    onClick = {
                        if (isUpvotedOrLiked){
                            isUpvotedOrLiked = false
                            onDislikeClick(postAndAnnouncement.id)
                        } else {
                            isUpvotedOrLiked = true
                            onLikeClick(postAndAnnouncement.id)
                        }
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isAnnouncement){
                            if (isUpvotedOrLiked){
                                Icon(
                                    painter = painterResource(R.drawable.filled_upvote_icon),
                                    contentDescription = null,
                                    tint = OrangeFilkom,
                                    modifier =  Modifier.requiredHeight(30.dp)
                                )
                            } else {
                                Icon(
                                    painter = painterResource(R.drawable.outlined_upvote_icon),
                                    contentDescription = null,
                                    modifier =  Modifier.requiredHeight(30.dp)
                                )
                            }
                        } else {
                            if (isUpvotedOrLikedByUser){
                                Icon(
                                    imageVector = Icons.Filled.ThumbUp,
                                    contentDescription = null,
                                    tint = OrangeFilkom,
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Outlined.ThumbUp,
                                    contentDescription = null,
                                )
                            }
                        }
                        Spacer(modifier = Modifier.requiredWidth(5.dp))
                        Text(
                            text = postAndAnnouncement.likes.toString(),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                TextButton(
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.Black
                    ),
                    onClick = { onPostClick(postAndAnnouncement.id, type) }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Comment,
                            contentDescription = null,
                        )
                        Spacer(modifier = Modifier.requiredWidth(5.dp))
                        Text(
                            text = postAndAnnouncement.comments.toString(),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            if (isOnDetail && postAndAnnouncement.description.isNotEmpty()){
                Divider(Modifier.padding(horizontal = 40.dp))
                Text(
                    text = postAndAnnouncement.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(20.dp)
                )
            }
        }
    }
}