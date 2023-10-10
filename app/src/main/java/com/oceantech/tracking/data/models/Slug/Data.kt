package com.oceantech.tracking.data.models.Slug

import com.google.gson.annotations.SerializedName


data class Data (

  @SerializedName("seoOnPage"  ) var seoOnPage  : SeoOnPage?            = SeoOnPage(),
  @SerializedName("breadCrumb" ) var breadCrumb : ArrayList<BreadCrumb> = arrayListOf(),
  @SerializedName("params"     ) var params     : Params?               = Params(),
  @SerializedName("item"       ) var item       : Item?                 = Item()

)