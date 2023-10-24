package dev.son.movie.network.models.vietsub

import com.google.gson.annotations.SerializedName


data class Data (

  @SerializedName("seoOnPage"            ) var seoOnPage         : SeoOnPage?            = SeoOnPage(),
  @SerializedName("breadCrumb"           ) var breadCrumb        : ArrayList<BreadCrumb> = arrayListOf(),
  @SerializedName("titlePage"            ) var titlePage         : String?               = null,
  @SerializedName("items"                ) var items             : ArrayList<Items>      = arrayListOf(),
  @SerializedName("params"               ) var params            : Params?               = Params(),
  @SerializedName("type_list"            ) var typeList          : String?               = null,
  @SerializedName("APP_DOMAIN_FRONTEND"  ) var APPDOMAINFRONTEND : String?               = null,
  @SerializedName("APP_DOMAIN_CDN_IMAGE" ) var APPDOMAINCDNIMAGE : String?               = null

)