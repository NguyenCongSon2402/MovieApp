package dev.son.movie.network.models.user

import com.google.gson.annotations.SerializedName

data class UserId(
    @SerializedName("avatar"          ) var avatar         : String?                   = null,
    @SerializedName("date_of_birth"   ) var dateOfBirth    : String?                   = null,
    @SerializedName("email"           ) var email          : String?                   = null,
    @SerializedName("viewing_history" ) var viewingHistory : ArrayList<ViewingHistory> = arrayListOf(),
    @SerializedName("favorite_movies" ) var favoriteMovies : FavoriteMovies?           = FavoriteMovies(),
    @SerializedName("watched_movies"  ) var watchedMovies  : WatchedMovies?            = WatchedMovies(),
    @SerializedName("name"            ) var name           : String?                   = null,
    @SerializedName("user_id"         ) var userId         : String?                   = null,
    @SerializedName("coins"         ) var coins         : Int?                   = 0
)