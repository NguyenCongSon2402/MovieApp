package dev.son.movie.network.models.movie

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: Status,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: T,
    @SerializedName("accessToken") val accessToken: String,
    var titlePage: String? = null
)
{

    override fun hashCode(): Int {
        // Kiểm tra xem titlePage có giá trị null không
        return titlePage?.hashCode() ?: 0
    }
}