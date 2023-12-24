package dev.son.movie.network.models.movie

import com.google.gson.annotations.SerializedName

data class  MovieDangChieu(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("director") val director: String,
    @SerializedName("releaseYear") val releaseYear: String,
    @SerializedName("duration") val duration: String,
    @SerializedName("posterHorizontal") val posterHorizontal: String,
    @SerializedName("posterVertical") val posterVertical: String,
    @SerializedName("actors") val actors: String,
    @SerializedName("trailerURL") val trailerURL: String,
    @SerializedName("genre") val genre: String,
    @SerializedName("country") val country: String,
    @SerializedName("hasFavorite") val hasFavorite: Boolean,
    @SerializedName("rating") val rating: Int?,
    @SerializedName("numberOfReviews") val numberOfReviews: Int?,
    @SerializedName("videoURL") val videoURL: List<String>?,
    @SerializedName("viewCounts") val viewCounts: Int?
)