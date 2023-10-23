package dev.son.movie.data.models.home

import com.google.gson.annotations.SerializedName


data class PhimBoDaHoanThanh (

  @SerializedName("status"  ) var status  : String? = null,
  @SerializedName("message" ) var message : String? = null,
  @SerializedName("data"    ) var data    : Data?   = Data()

)