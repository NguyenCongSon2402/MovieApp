package dev.son.movie.network.models.user

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("birthday") var birthday: String? = null,
    @SerializedName("photoURL") var photoURL: String? = null,
    @SerializedName("isAdmin") var isAdmin: Boolean? = null,
    @SerializedName("createdAt") var createdAt: String? = null,
    @SerializedName("updatedAt") var updatedAt: String? = null,
    @SerializedName("coins") var coins: Int? = 0
)
