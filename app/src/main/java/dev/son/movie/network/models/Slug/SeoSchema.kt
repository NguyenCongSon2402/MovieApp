package dev.son.movie.network.models.Slug

import com.google.gson.annotations.SerializedName


data class SeoSchema (

  @SerializedName("@context"      ) var context      : String? = null,
  @SerializedName("@type"         ) var type         : String? = null,
  @SerializedName("name"          ) var name          : String? = null,
  @SerializedName("dateModified"  ) var dateModified  : String? = null,
  @SerializedName("dateCreated"   ) var dateCreated   : String? = null,
  @SerializedName("url"           ) var url           : String? = null,
  @SerializedName("datePublished" ) var datePublished : String? = null,
  @SerializedName("image"         ) var image         : String? = null,
  @SerializedName("director"      ) var director      : String? = null

)