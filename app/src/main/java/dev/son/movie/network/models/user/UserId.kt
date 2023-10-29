package dev.son.movie.network.models.user

import com.google.gson.annotations.SerializedName

data class UserId(
    @SerializedName("avatar"             ) var avatar            : String?            = null,
    @SerializedName("date_of_birth"      ) var dateOfBirth       : String?            = null,
    @SerializedName("email"              ) var email             : String?            = null,
    @SerializedName("movie_trackinglist" ) var movieTrackinglist : MovieTrackinglist? = MovieTrackinglist(),
    @SerializedName("name"               ) var name              : String?            = null,
    @SerializedName("user_id"            ) var userId            : String?            = null
)