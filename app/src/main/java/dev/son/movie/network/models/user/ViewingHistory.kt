package dev.son.movie.network.models.user

import com.google.gson.annotations.SerializedName

data class ViewingHistory (

    @SerializedName("slug"      ) var slug      : String?             = null,
    @SerializedName("type"      ) var type      : String?             = null,
    @SerializedName("movie_id"  ) var movieId   : String?             = null,
    @SerializedName("category"  ) var category  : ArrayList<Category> = arrayListOf(),
    @SerializedName("timestamp" ) var timestamp : String?             = null

)
