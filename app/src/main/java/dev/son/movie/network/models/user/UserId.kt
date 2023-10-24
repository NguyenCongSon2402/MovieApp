package dev.son.movie.network.models.user

import com.google.gson.annotations.SerializedName

data class UserId(
    @SerializedName("user_id"         ) var userId         : String?                   = null,
    @SerializedName("avatar"          ) var avatar         : String?                   = null,
    @SerializedName("date_of_birth"   ) var dateOfBirth    : String?                   = null,
    @SerializedName("email"           ) var email          : String?                   = null,
    @SerializedName("name"            ) var name           : String?                   = null,
    @SerializedName("favorite_movies" ) var favoriteMovies : ArrayList<String>         = arrayListOf(),
    @SerializedName("movie_ratings"   ) var movieRatings   : ArrayList<MovieRatings>   = arrayListOf(),
    @SerializedName("viewing_history" ) var viewingHistory : ArrayList<ViewingHistory> = arrayListOf(),
    @SerializedName("watched_movies"  ) var watchedMovies  : ArrayList<String>         = arrayListOf()
)