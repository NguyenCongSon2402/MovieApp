package dev.son.movie.network.models.rate

import com.google.gson.annotations.SerializedName

data class RateRespone(
    @SerializedName("id"        ) var id        : Int?    = null,
    @SerializedName("rating"    ) var rating    : Int?    = 0,
    @SerializedName("createdAt" ) var createdAt : String? = null,
    @SerializedName("updatedAt" ) var updatedAt : String? = null,
    @SerializedName("userId"    ) var userId    : Int?    = null,
    @SerializedName("movieId"   ) var movieId   : Int?    = null
)
