package dev.son.movie.network.models.user

import com.google.gson.annotations.SerializedName

data class FavoriteMovies(
    @SerializedName("movie_id_1" ) var movieId1 : MovieId1? = MovieId1()
)
