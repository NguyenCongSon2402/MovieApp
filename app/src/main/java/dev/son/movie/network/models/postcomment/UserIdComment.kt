package dev.son.movie.network.models.postcomment

import com.google.gson.annotations.SerializedName

data class UserIdComment(
    @SerializedName("user_id_1") var userId1: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("text") var text: String? = null,
    @SerializedName("timestamp") var timestamp: String? = null,
    @SerializedName("comment_id") var commentId: String? = null
)
