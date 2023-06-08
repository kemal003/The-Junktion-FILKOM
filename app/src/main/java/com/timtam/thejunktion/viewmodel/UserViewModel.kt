package com.timtam.thejunktion.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.timtam.thejunktion.model.UserData
import com.timtam.thejunktion.model.UserDataResponse
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserViewModel (
    private val stateHandle: SavedStateHandle
): ViewModel(){

    private var auth: FirebaseAuth = Firebase.auth
    private var db: FirebaseFirestore = Firebase.firestore

    val userData = mutableStateOf<UserData?>(null)

    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        exception.printStackTrace()
    }

    init {
        val savedUserDataMap = stateHandle.get<HashMap<String, String>>(USERDATA)
        println(savedUserDataMap)
        if (savedUserDataMap == null) {
            buildUserData()
        } else {
            userData.value = restoreUserData(savedUserDataMap)
        }
    }

    private fun buildUserData(){
        viewModelScope.launch(Dispatchers.IO + errorHandler){
            val userDataResult = getUserData()
            userData.value = userDataResult
        }
    }

    private suspend fun getUserData(): UserData? {
        return withContext(Dispatchers.IO){
            try {
                if (auth.uid != null){
                    val snapshot = db.collection("users")
                        .document(auth.uid!!).get().await()
                    if (snapshot.exists()) {
                        val data = snapshot.data!!
                        val userData = UserDataResponse(
                            name = data["name"],
                            email = data["email"],
                            id = auth.uid,
                            nim = data["nim"],
                            photoUrl = data["photoUrl"],
                            prodi = data["prodi"],
                        ).toUserData()
                        storeUserData(userData.toUserMap())
                        return@withContext userData
                    } else {
                        return@withContext null
                    }
                } else {
                    return@withContext null
                }
            } catch (e: Exception){
                return@withContext null
            }
        }
    }

    private fun storeUserData(userData: HashMap<String, String>){
        stateHandle[USERDATA] = userData
        val savedUserDataMap = stateHandle.get<HashMap<String, String>>(USERDATA)
        println(savedUserDataMap.toString())
    }

    private fun restoreUserData(data: HashMap<String, String>): UserData{
        return UserData(
            name = data["name"].orEmpty(),
            email = data["email"].orEmpty(),
            id = data["id"].orEmpty(),
            nim = data["nim"].orEmpty(),
            photoUrl = data["photoUrl"].orEmpty(),
            prodi = data["prodi"].orEmpty(),
        )
    }

    companion object {
        private const val USERDATA = "userData"
    }
}