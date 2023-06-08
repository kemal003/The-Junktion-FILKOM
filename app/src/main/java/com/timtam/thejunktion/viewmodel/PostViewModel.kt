package com.timtam.thejunktion.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.timtam.thejunktion.model.Comment
import com.timtam.thejunktion.model.CommentResponse
import com.timtam.thejunktion.model.PostAndAnnouncement
import com.timtam.thejunktion.model.PostAndAnnouncementResponse
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PostViewModel(
    private val stateHandle: SavedStateHandle
): ViewModel() {

    private var auth: FirebaseAuth = Firebase.auth
    private var db: FirebaseFirestore = Firebase.firestore
    private var storage: StorageReference = Firebase.storage.reference

    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        exception.printStackTrace()
    }

    val allPostsList = mutableStateOf(emptyList<PostAndAnnouncement>())
    val allPostsLikedId = mutableStateOf(emptyList<String>())
    val allCommentOfPost = mutableStateOf(emptyList<Comment>())
    val detailsById = mutableStateOf<PostAndAnnouncement?>(null)

    var isSnackbarShown: Boolean by mutableStateOf(false)
        private set

    private fun showSnackbar() {
        isSnackbarShown = true
    }

    fun buildPostOrAnnouncement(
        onCompleteCallback: () -> Unit,
        onErrorCallback: () -> Unit,
        type: String,
        orderBy: String = "timestamp",
    ){
        viewModelScope.launch(errorHandler) {
            try {
                val allPosts = getAllPostsOrAnnouncement(type, orderBy)
                allPostsList.value = allPosts
                withContext(Dispatchers.Main){
                    onCompleteCallback()
                }
            } catch (e: Exception){
                withContext(Dispatchers.Main){
                    onErrorCallback()
                }
            }
        }
    }

    private suspend fun getAllPostsOrAnnouncement(
        type: String,
        orderBy: String = "timestamp"
    ): List<PostAndAnnouncement>{
        return withContext(Dispatchers.IO){
            try {
                val task = db.collection(type)
                    .orderBy(orderBy, Query.Direction.DESCENDING)
                    .get().await()
                val posts = task.documents
                val temp = mutableListOf<PostAndAnnouncement>()
                if (posts.isNotEmpty()){
                    posts.forEach { snapshot ->
                        if (snapshot.data != null){
                            val snapshotData = snapshot.data!!
                            temp.add(
                                PostAndAnnouncementResponse(
                                    id = snapshot.id,
                                    sender = snapshotData["sender"],
                                    timestamp = snapshotData["timestamp"],
                                    title = snapshotData["title"],
                                    description = snapshotData["description"],
                                    likes = snapshotData["likes"],
                                    comments = snapshotData["comments"],
                                    photoUrl = snapshotData["photoUrl"],
                                    contact = snapshotData["contact"],
                                ).toPostAndAnnouncement()
                            )
                        }
                    }
                    return@withContext temp
                }
                return@withContext emptyList<PostAndAnnouncement>()
            } catch (e: Exception){
                e.printStackTrace()
                return@withContext emptyList<PostAndAnnouncement>()
            }
        }
    }

    fun buildLikeDetails() {
        val storedLikes = stateHandle.get<List<String>?>(LIKES)
        if (!storedLikes.isNullOrEmpty()){
            allPostsLikedId.value = storedLikes
        } else {
            viewModelScope.launch(errorHandler){
                val allLikesDetail = getAllLikesDetail()
                allPostsLikedId.value = allLikesDetail
                stateHandle[LIKES] = allLikesDetail
            }
        }
    }

    private fun toggleLike(id: String, isLiked: Boolean, isListSaving: Boolean){
        if (isListSaving){
            val listPosts = allPostsList.value.toMutableList()
            val postIndex = listPosts.indexOfFirst { it.id == id }
            val post = listPosts[postIndex]
            listPosts[postIndex] = post.copy(
                likes = if (isLiked) (post.likes + 1) else (post.likes - 1)
            )
            allPostsList.value = listPosts
        } else {
            val likeCount = detailsById.value?.likes
            detailsById.value = detailsById.value?.copy(
                likes = if (isLiked) (likeCount ?: 0) + 1 else (likeCount ?: 0) - 1
            )
        }

        val listLike = allPostsLikedId.value.toMutableList()
        if (isLiked) listLike.add(id) else listLike.remove(id)
        allPostsLikedId.value = listLike

        val likedList = stateHandle.get<List<String>?>(LIKES).orEmpty().toMutableList()
        if (isLiked) likedList.add(id) else likedList.remove(id)
        stateHandle[LIKES] = likedList
    }

    fun likePost(id: String, type: String, isListSaving: Boolean = true){
        toggleLike(id, true, isListSaving)
        viewModelScope.launch(Dispatchers.IO + errorHandler){
            db.collection("users").document(auth.uid!!).update(
                "likeDetails", FieldValue.arrayUnion(id)
            )
            db.collection(type).document(id).update(
                "likes", FieldValue.increment(1)
            )
        }
    }

    fun dislikePost(id: String, type: String, isListSaving: Boolean = true){
        toggleLike(id, false, isListSaving)
        viewModelScope.launch(Dispatchers.IO + errorHandler){
            db.collection("users").document(auth.uid!!).update(
                "likeDetails", FieldValue.arrayRemove(id)
            )
            db.collection(type).document(id).update(
                "likes", FieldValue.increment(-1)
            )
        }
    }

    private suspend fun getAllLikesDetail(): List<String> {
        return withContext(Dispatchers.IO){
            try {
                val snapshot = db.collection("users").document(auth.uid!!).get().await()
                if (snapshot.exists()){
                    val likeDetailsAny = snapshot.data?.get("likeDetails") as List<*>
                    val likeDetails = mutableListOf<String>()
                    likeDetailsAny.let {
                        it.forEach { postId ->
                            likeDetails.add(postId as String)
                        }
                    }
                    return@withContext likeDetails
                } else {
                    return@withContext emptyList<String>()
                }
            } catch (e: Exception){
                return@withContext emptyList<String>()
            }
        }
    }

    fun buildDetailById(
        onCompleteCallback: () -> Unit,
        onErrorCallback: () -> Unit,
        id: String,
        type: String
    ){
        viewModelScope.launch(errorHandler){
            try {
                val details = getDetailById(id, type)
                detailsById.value = details

                val comments = getCommentById(id, type)
                allCommentOfPost.value = comments

                withContext(Dispatchers.Main){
                    onCompleteCallback()
                }
            } catch (e: Exception){
                withContext(Dispatchers.Main){
                    onErrorCallback()
                }
            }
        }
    }

    private suspend fun getDetailById(id: String, type: String): PostAndAnnouncement?{
        return withContext(Dispatchers.IO){
            try {
                val snapshot = db.collection(type).document(id).get().await()
                if (snapshot.exists()){
                    val snapshotData = snapshot.data!!
                    return@withContext PostAndAnnouncementResponse(
                        id = snapshot.id,
                        sender = snapshotData["sender"],
                        timestamp = snapshotData["timestamp"],
                        title = snapshotData["title"],
                        description = snapshotData["description"],
                        likes = snapshotData["likes"],
                        comments = snapshotData["comments"],
                        photoUrl = snapshotData["photoUrl"],
                        contact = snapshotData["contact"],
                    ).toPostAndAnnouncement()
                }
                return@withContext null
            } catch (e: Exception){
                return@withContext null
            }
        }
    }

    private suspend fun getCommentById(id: String, type: String): List<Comment>{
        return withContext(Dispatchers.IO){
            try {
                val task = db.collection(type).document(id).collection("comments")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get().await()
                val comments = task.documents
                val temp = mutableListOf<Comment>()
                if (comments.isNotEmpty()) {
                    comments.forEach { snapshot ->
                        if (snapshot.data != null) {
                            val snapshotData = snapshot.data!!
                            temp.add(
                                CommentResponse(
                                    id = snapshot.id,
                                    sender = snapshotData["sender"],
                                    content = snapshotData["content"],
                                    timestamp = snapshotData["timestamp"],
                                ).toComment()
                            )
                        }
                    }
                    return@withContext temp
                }
                return@withContext emptyList<Comment>()
            } catch (e: Exception){
                return@withContext emptyList<Comment>()
            }
        }
    }

    suspend fun addComment(
        type: String,
        id: String,
        name: String,
        content: String,
        onCompleteCallback: () -> Unit,
        onErrorCallback: (String) -> Unit,
    ){
        withContext(Dispatchers.IO + errorHandler){
            try {
                db.collection(type).document(id)
                    .update(
                        "comments", FieldValue.increment(1)
                    )
                db.collection(type).document(id)
                    .collection("comments").add(
                        hashMapOf(
                            "sender" to name,
                            "content" to content,
                            "timestamp" to FieldValue.serverTimestamp(),
                        )
                    ).await()

                val comments = getCommentById(id, type)
                allCommentOfPost.value = comments

                val commentCount = detailsById.value?.comments
                detailsById.value = detailsById.value?.copy(comments = (commentCount ?: 0) + 1)

                withContext(Dispatchers.Main){
                    onCompleteCallback()
                }
            } catch (e: Exception){
                withContext(Dispatchers.Main){
                    onErrorCallback("Gagal mengirim")
                }
            }
        }
    }

    suspend fun newPostOrAnnouncement(
        path: String,
        name: String,
        title: String? = null,
        description: String? = null,
        photo: Uri?,
        contact: String? = null,
        onCompleteCallback: () -> Unit,
        onErrorCallback: (String) -> Unit,
    ){
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        val currentTime = LocalDateTime.now().format(formatter)
        val uploadRef = storage.child(path).child("${auth.uid}_$currentTime.jpg")
        withContext(Dispatchers.IO + errorHandler){
            try {
                val toBeSent = hashMapOf(
                    "sender" to name,
                    "title" to title,
                    "description" to description,
                    "timestamp" to FieldValue.serverTimestamp(),
                    "likes" to 0,
                    "comments" to 0,
                )
                if (title != null){
                    toBeSent["title"] = title
                }
                if (description != null){
                    toBeSent["description"] = description
                }
                if (contact != null){
                    toBeSent["contact"] = contact
                }
                if (photo != null){
                    val uploadTask = uploadRef.putFile(photo).await()

                    if (uploadTask.error == null){
                        val photoUrl = uploadTask.storage.downloadUrl.await()
                        toBeSent["photoUrl"] = photoUrl
                    } else {
                        withContext(Dispatchers.Main){
                            onErrorCallback(uploadTask.error?.message ?: "Terjadi kesalahan")
                            showSnackbar()
                        }
                    }
                }

                db.collection(path).add(toBeSent).await()
                withContext(Dispatchers.Main){
                    showSnackbar()
                    onCompleteCallback()
                }
            } catch (e: Exception){
                withContext(Dispatchers.Main){
                    onErrorCallback(e.message ?: "Terjadi kesalahan")
                    showSnackbar()
                }
            }

        }
    }

    companion object {
        const val LIKES = "likes"
    }

}