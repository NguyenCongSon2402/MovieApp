package dev.son.movie.network.models.Slug

import com.google.gson.annotations.SerializedName


data class SeoOnPage (

  @SerializedName("og_type"         ) var ogType          : String?           = null,
  @SerializedName("titleHead"       ) var titleHead       : String?           = null,
  @SerializedName("seoSchema"       ) var seoSchema       : SeoSchema?        = SeoSchema(),
  @SerializedName("descriptionHead" ) var descriptionHead : String?           = null,
  @SerializedName("og_image"        ) var ogImage         : ArrayList<String> = arrayListOf(),
  @SerializedName("updated_time"    ) var updatedTime     : Long?              = null,
  @SerializedName("og_url"          ) var ogUrl           : String?           = null

)