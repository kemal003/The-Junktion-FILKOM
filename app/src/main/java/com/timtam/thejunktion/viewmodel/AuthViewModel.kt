package com.timtam.thejunktion.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

@HiltViewModel
class AuthViewModel(
    private var auth: FirebaseAuth,
    private var db: FirebaseFirestore,
    private val stateHandle: SavedStateHandle
) : ViewModel() {

    private var currentUser: FirebaseUser?

    init {
        auth = Firebase.auth
        currentUser = auth.currentUser
        db = Firebase.firestore
    }

    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        exception.printStackTrace()
    }

    fun isLoggedIn(): Boolean {
        return currentUser != null
    }

    fun signUp(
        email: String,
        password: String,
        name: String,
        registerCallBack: () -> Unit,
    ){
        viewModelScope.launch(errorHandler){
            createAccount(email, password, name, registerCallBack)
        }
    }

    private suspend fun createAccount(
        email: String,
        password: String,
        name: String,
        registerCallBack: () -> Unit,
    ){
        return withContext(Dispatchers.IO){
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                db.collection("users").add(
                    hashMapOf(
                        "name" to name,
                        "createdAt" to FieldValue.serverTimestamp(),
                    )
                ).await()
                currentUser = result?.user
                withContext(Dispatchers.Main){
                    registerCallBack()
                }
            } catch (e: Exception){
                throw e;
            }
        }
    }

    fun signIn(email: String, password: String){
        viewModelScope.launch(errorHandler){
            login(email, password)
        }
    }

    private suspend fun login(
        email: String,
        password: String,
    ){
        return withContext(Dispatchers.IO){
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                currentUser = result?.user
            } catch (e: Exception){
                throw e;
            }
        }
    }

    fun signOut(){
        auth.signOut()
    }
}