package dev.son.movie.network.models.movie

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Movie(
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
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.createStringArrayList(),
        parcel.readValue(Int::class.java.classLoader) as? Int
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(director)
        parcel.writeString(releaseYear)
        parcel.writeString(duration)
        parcel.writeString(posterHorizontal)
        parcel.writeString(posterVertical)
        parcel.writeString(actors)
        parcel.writeString(trailerURL)
        parcel.writeString(genre)
        parcel.writeString(country)
        parcel.writeByte(if (hasFavorite) 1 else 0)
        parcel.writeValue(rating)
        parcel.writeValue(numberOfReviews)
        parcel.writeStringList(videoURL)
        parcel.writeValue(viewCounts)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Movie> {
        override fun createFromParcel(parcel: Parcel): Movie {
            return Movie(parcel)
        }

        override fun newArray(size: Int): Array<Movie?> {
            return arrayOfNulls(size)
        }
    }
}