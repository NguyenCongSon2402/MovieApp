package dev.son.movie.network.models.movie

import com.google.gson.annotations.SerializedName

data class MovieByGenre(
    @SerializedName("code"   ) var code   : Int?            = null,
    @SerializedName("status" ) var status : String?         = null,
    @SerializedName("data"   ) var data   : ArrayList<Movie> = arrayListOf()
)
