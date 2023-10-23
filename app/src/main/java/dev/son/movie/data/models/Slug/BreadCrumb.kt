package dev.son.movie.data.models.Slug

import com.google.gson.annotations.SerializedName


data class BreadCrumb (

  @SerializedName("name"     ) var name     : String? = null,
  @SerializedName("slug"     ) var slug     : String? = null,
  @SerializedName("position" ) var position : Int?    = null

)