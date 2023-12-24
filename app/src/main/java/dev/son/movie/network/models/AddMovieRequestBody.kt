package dev.son.movie.network.models

import com.google.gson.annotations.SerializedName

data class AddMovieRequestBody(
    @SerializedName("title") var title: String ?=null,
    @SerializedName("description") var description: String ?=null,
    @SerializedName("director") var director: String ?=null,
    @SerializedName("releaseYear") var releaseYear: String ?=null,
    @SerializedName("duration") var duration: String ?=null,
    @SerializedName("country") var country: String ?=null,
    @SerializedName("actors") var actors: String ?=null,
    @SerializedName("trailerURL") var trailerURL: String ?=null,
    @SerializedName("genre") var genre: List<String> ?=null,
    @SerializedName("videoURL") var videoURL: List<String> ?=null,
    @SerializedName("posterVertical") var posterVertical: String ?=null,
    @SerializedName("posterHorizontal") var posterHorizontal: String ?=null
)
