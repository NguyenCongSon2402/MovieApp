package dev.son.movie.network.models.movie;

import com.google.gson.annotations.SerializedName;

public enum Status {
    @SerializedName("Success")
    SUCCESS,
    @SerializedName("Error")
    ERROR
}
