package com.oceantech.tracking.data.models.phimbo

import com.google.gson.annotations.SerializedName


data class BreadCrumb (

  @SerializedName("name"      ) var name      : String?  = null,
  @SerializedName("slug"      ) var slug      : String?  = null,
  @SerializedName("isCurrent" ) var isCurrent : Boolean? = null,
  @SerializedName("position"  ) var position  : Int?     = null

)