package dev.son.movie.network.models.home

import com.google.gson.annotations.SerializedName


data class PhimSapChieu (

  @SerializedName("status"  ) var status  : String? = null,
  @SerializedName("message" ) var message : String? = null,
  @SerializedName("data"    ) var data    : Data?   = Data()

)