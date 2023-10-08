package com.oceantech.tracking.data.models

import com.google.gson.annotations.SerializedName


data class PhimHoatHinh (

  @SerializedName("status"  ) var status  : String? = null,
  @SerializedName("message" ) var message : String? = null,
  @SerializedName("data"    ) var data    : Data?   = Data()

)