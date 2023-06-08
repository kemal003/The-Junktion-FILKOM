package com.timtam.thejunktion.model

import com.google.firebase.Timestamp
import java.util.Date

data class PostAndAnnouncementResponse(
    val id: Any? = null,
    val sender: Any? = null,
    val timestamp: Any? = null,
    val title: Any? = null,
    val description: Any? = null,
    val likes: Any? = null,
    val comments: Any? = null,
    val photoUrl: Any? = null,
    val contact: Any? = null,
){
    fun toPostAndAnnouncement() = PostAndAnnouncement(
        id = (id as String?).orEmpty(),
        sender = (sender as String?).orEmpty(),
        timestamp = (timestamp as Timestamp).toDate(),
        title = (title as String?).orEmpty(),
        description = (description as String?).orEmpty(),
        likes = (likes as Long?) ?: 0,
        comments = (comments as Long?) ?: 0,
        photoUrl = (photoUrl as String?).orEmpty(),
        contact = (contact as String?).orEmpty()
    )
}

data class PostAndAnnouncement(
    val id: String,
    val sender: String,
    val timestamp: Date,
    val title: String,
    val description: String,
    val likes: Long,
    val comments: Long,
    val photoUrl: String,
    val contact: String,
)