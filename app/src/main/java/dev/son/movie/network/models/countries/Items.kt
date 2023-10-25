package dev.son.movie.network.models.countries

import com.google.gson.annotations.SerializedName


data class Items (

  @SerializedName("_id"  ) var Id   : String? = null,
  @SerializedName("name" ) var name : String? = null,
  @SerializedName("slug" ) var slug : String? = null

  )