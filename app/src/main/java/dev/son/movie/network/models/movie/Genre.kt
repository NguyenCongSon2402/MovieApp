package dev.son.movie.network.models.movie

import com.google.gson.annotations.SerializedName

data class Genre(
    @SerializedName("name") val name: String? = null,
    @SerializedName("code") val code: String? = null,
    var selected: Boolean = false
)
