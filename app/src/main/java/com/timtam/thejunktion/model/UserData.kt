package com.timtam.thejunktion.model

data class UserDataResponse(
    val id: Any? = null,
    val name: Any? = null,
    val email: Any? = null,
    val nim: Any? = null,
    val photoUrl: Any? = null,
    val prodi: Any? = null,
) {
    fun toUserData() = UserData(
        id = (id as String?).orEmpty(),
        name = (name as String?).orEmpty(),
        email = (email as String?).orEmpty(),
        nim = (nim  as String?).orEmpty(),
        photoUrl = (photoUrl  as String?).orEmpty(),
        prodi = (prodi as String?).orEmpty(),
    )
}

data class UserData(
    val id: String,
    val name: String,
    val email: String,
    val nim: String,
    val photoUrl: String,
    val prodi: String,
) {
    fun toUserMap() = hashMapOf(
        "id" to id,
        "name" to name,
        "email" to email,
        "nim" to nim,
        "photoUrl" to photoUrl,
        "prodi" to prodi,
    )
}