package com.timtam.thejunktion.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.Exception

class AuthViewModel(
    private val stateHandle: SavedStateHandle
) : ViewModel() {

    private var currentUser: FirebaseUser?
    private var auth: FirebaseAuth = Firebase.auth
    private var db: FirebaseFirestore = Firebase.firestore

    var isSnackbarShown: Boolean by mutableStateOf(false)
        private set

    init {
        currentUser = auth.currentUser
    }

    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        exception.printStackTrace()
    }

    fun isLoggedIn(): Boolean {
        println(currentUser?.uid)
        return currentUser != null
    }

    private fun showSnackbar() {
        isSnackbarShown = true
    }

    suspend fun signUp(
        email: String,
        password: String,
        name: String,
        onCompleteCallBack: () -> Unit,
        onErrorCallback: (String) -> Unit,
    ){
        withContext(Dispatchers.IO + errorHandler){
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                db.collection("users").document(auth.uid!!).set(
                    hashMapOf(
                        "name" to name,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "email" to email,
                        "nim" to null,
                        "photoUrl" to null,
                        "prodi" to null,
                    )
                ).await()
                currentUser = result?.user
                withContext(Dispatchers.Main){
                    onCompleteCallBack()
                }
            } catch (e: FirebaseAuthException){
                withContext(Dispatchers.Main){
                    onErrorCallback(errorTranslator(e.errorCode))
                    showSnackbar()
                }
            } catch (e: Exception){
                withContext(Dispatchers.Main){
                    onErrorCallback("Terjadi kesalahan")
                }
            }
        }
    }

    suspend fun signIn(
        email: String,
        password: String,
        onErrorCallback: (String) -> Unit,
        onCompleteCallBack: () -> Unit,
    ){
        withContext(Dispatchers.IO + errorHandler){
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                currentUser = result?.user
                withContext(Dispatchers.Main){
                    onCompleteCallBack()
                }
            } catch (e: FirebaseAuthException){
                onErrorCallback(errorTranslator(e.errorCode))
                showSnackbar()
            } catch (e: Exception){
                onErrorCallback("Terjadi kesalahan")
                showSnackbar()
            }
        }
    }

    fun signOut(
        onCompleteCallBack: () -> Unit,
    ){
        viewModelScope.launch {
            auth.signOut()
            withContext(Dispatchers.Main){
                onCompleteCallBack()
            }
        }
    }

    private fun errorTranslator(errorCode: String) : String{
        return when (errorCode){
            "ERROR_INVALID_EMAIL" -> "Email yang dimasukkan tidak valid"
            "ERROR_WRONG_PASSWORD" -> "Password salah"
            "ERROR_EMAIL_ALREADY_IN_USE" -> "Email sudah terpakai"
            "ERROR_USER_NOT_FOUND" -> "Pengguna tidak terdaftar"
            "ERROR_USER_DISABLED" -> "Pengguna diblokir"
            else -> {
                "Terjadi kesalahan"
            }
        }
    }
}