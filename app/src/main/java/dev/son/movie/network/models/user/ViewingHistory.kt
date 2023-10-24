package dev.son.movie.network.models.user

import com.google.gson.annotations.SerializedName

data class ViewingHistory (

    @SerializedName("history_id" ) var historyId : String? = null,
    @SerializedName("movie_id"   ) var movieId   : String? = null,
    @SerializedName("timestamp"  ) var timestamp : String? = null

)
