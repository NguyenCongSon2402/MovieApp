package dev.son.movie.data.models.tvshow

import com.google.gson.annotations.SerializedName


data class Category (

  @SerializedName("id"   ) var id   : String? = null,
  @SerializedName("name" ) var name : String? = null,
  @SerializedName("slug" ) var slug : String? = null

)