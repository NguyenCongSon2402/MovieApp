package dev.son.movie.network.models.Favorite

import com.google.gson.annotations.SerializedName

data class addFavorite(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("movieId") var movieId: String? = null,
    @SerializedName("userId") var userId: Int? = null,
    @SerializedName("updatedAt") var updatedAt: String? = null,
    @SerializedName("createdAt") var createdAt: String? = null
)
