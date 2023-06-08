package com.timtam.thejunktion.model

import com.google.firebase.Timestamp
import java.util.Date

data class CommentResponse(
    val id: Any? = null,
    val sender: Any? = null,
    val content: Any? = null,
    val timestamp: Any? = null,
){
    fun toComment() = Comment(
        id = (id as String?).orEmpty(),
        sender = (sender as String?).orEmpty(),
        content = (content as String?).orEmpty(),
        timestamp = (timestamp as Timestamp).toDate(),
    )
}

data class Comment(
    val id: String,
    val sender: String,
    val content: String,
    val timestamp: Date,
)