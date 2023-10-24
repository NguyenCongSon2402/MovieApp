package dev.son.movie.network.models.user

import com.google.gson.annotations.SerializedName

data class MovieRatings (

    @SerializedName("movie_id" ) var movieId : String? = null,
    @SerializedName("rating"   ) var rating  : Int?    = null

)
