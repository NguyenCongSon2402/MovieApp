package dev.son.movie.network.models.categorymovie

import com.google.gson.annotations.SerializedName


data class Country (

  @SerializedName("id"   ) var id   : String? = null,
  @SerializedName("name" ) var name : String? = null,
  @SerializedName("slug" ) var slug : String? = null

)