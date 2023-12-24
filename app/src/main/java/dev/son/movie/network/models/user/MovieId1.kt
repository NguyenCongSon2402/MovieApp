package dev.son.movie.network.models.user

import com.google.gson.annotations.SerializedName

data class MovieId1(

    @SerializedName("movie_id_1") var movieId1: String? = null,
    @SerializedName("slug") var slug: String? = null,
    @SerializedName("type") var type: String? = null,
    @SerializedName("thumb_url") var thumbUrl: String? = null,
    @SerializedName("category") var category: ArrayList<Category> = arrayListOf()

)
