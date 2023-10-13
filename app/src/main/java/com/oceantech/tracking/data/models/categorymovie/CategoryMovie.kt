package com.oceantech.tracking.data.models.categorymovie

import com.google.gson.annotations.SerializedName


data class CategoryMovie (

  @SerializedName("status"  ) var status  : String? = null,
  @SerializedName("message" ) var message : String? = null,
  @SerializedName("data"    ) var data    : Data?   = Data()

)