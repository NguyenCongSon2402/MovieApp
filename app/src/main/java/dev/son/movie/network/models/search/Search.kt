package dev.son.movie.network.models.search

import com.google.gson.annotations.SerializedName
import dev.son.movie.network.models.home.Data


data class Search (

  @SerializedName("status"  ) var status  : String? = null,
  @SerializedName("message" ) var message : String? = null,
  @SerializedName("data"    ) var data    : Data?   = Data()

)