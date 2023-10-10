package com.oceantech.tracking.data.models.Slug

import com.google.gson.annotations.SerializedName


data class Slug (

  @SerializedName("status"  ) var status  : String? = null,
  @SerializedName("message" ) var message : String? = null,
  @SerializedName("data"    ) var data    : Data?   = Data()

)