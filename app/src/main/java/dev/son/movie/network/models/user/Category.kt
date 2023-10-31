package dev.son.movie.network.models.user

import com.google.gson.annotations.SerializedName

data class Category (

    @SerializedName("name" ) var name : String? = null,
    @SerializedName("slug" ) var slug : String? = null

)
