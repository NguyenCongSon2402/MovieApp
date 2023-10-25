package dev.son.movie.network.models.categories

import com.google.gson.annotations.SerializedName
import dev.son.movie.network.models.countries.Data


data class Categories (

  @SerializedName("status"  ) var status  : String? = null,
  @SerializedName("message" ) var message : String? = null,
  @SerializedName("data"    ) var data    : Data?   = Data()

)