package dev.son.movie.data.models.vietsub

import com.google.gson.annotations.SerializedName


data class SeoOnPage (

  @SerializedName("og_type"         ) var ogType          : String?           = null,
  @SerializedName("titleHead"       ) var titleHead       : String?           = null,
  @SerializedName("descriptionHead" ) var descriptionHead : String?           = null,
  @SerializedName("og_image"        ) var ogImage         : ArrayList<String> = arrayListOf(),
  @SerializedName("og_url"          ) var ogUrl           : String?           = null

)