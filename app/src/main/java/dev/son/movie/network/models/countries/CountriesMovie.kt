package dev.son.movie.network.models.countries

import com.google.gson.annotations.SerializedName
import dev.son.movie.network.models.categorymovie.Data


data class CountriesMovie (

  @SerializedName("status"  ) var status  : String? = null,
  @SerializedName("message" ) var message : String? = null,
  @SerializedName("data"    ) var data    : Data? =Data()

)