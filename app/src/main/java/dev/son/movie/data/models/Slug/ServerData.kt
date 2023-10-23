package dev.son.movie.data.models.Slug

import com.google.gson.annotations.SerializedName


data class ServerData (

  @SerializedName("name"       ) var name      : String? = null,
  @SerializedName("slug"       ) var slug      : String? = null,
  @SerializedName("filename"   ) var filename  : String? = null,
  @SerializedName("link_embed" ) var linkEmbed : String? = null,
  @SerializedName("link_m3u8"  ) var linkM3u8  : String? = null

)