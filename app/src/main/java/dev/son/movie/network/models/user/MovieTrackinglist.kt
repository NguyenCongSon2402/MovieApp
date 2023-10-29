package dev.son.movie.network.models.user

import com.google.gson.annotations.SerializedName

data class MovieTrackinglist (

    @SerializedName("viewing_history" ) var viewingHistory : ArrayList<ViewingHistory> = arrayListOf(),
    @SerializedName("watched_movies"  ) var watchedMovies  : ArrayList<String>         = arrayListOf(),
    @SerializedName("favorite_movies" ) var favoriteMovies : ArrayList<String>         = arrayListOf()

)
