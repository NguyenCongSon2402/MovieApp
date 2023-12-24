package dev.son.movie.network.models.user

import com.google.gson.annotations.SerializedName

data class TokenResponse(
    @SerializedName("code"        ) var code        : Int?    = null,
    @SerializedName("status"      ) var status      : String? = null,
    @SerializedName("message"     ) var message     : String? = null,
    @SerializedName("data"        ) var data        : User?   = User(),
    @SerializedName("accessToken" ) var accessToken : String? = null
)