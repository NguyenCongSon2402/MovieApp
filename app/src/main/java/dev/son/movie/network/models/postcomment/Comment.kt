package dev.son.movie.network.models.postcomment

import com.google.gson.annotations.SerializedName
import dev.son.movie.network.models.user.User
import java.util.Date

data class Comment(
    @SerializedName("id") val id: Int? =null,
    @SerializedName("comment") val comment: String? =null,
    @SerializedName("updatedAt") val updatedAt: String? =null,
    @SerializedName("user") var user: User?=User(),
    @SerializedName("createdAt") val createdAt: String? =null
)