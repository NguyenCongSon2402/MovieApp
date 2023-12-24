package dev.son.movie.network.models.search

import com.google.gson.annotations.SerializedName


data class Search (

  @SerializedName("status"  ) var status  : String? = null,
  @SerializedName("message" ) var message : String? = null,

)