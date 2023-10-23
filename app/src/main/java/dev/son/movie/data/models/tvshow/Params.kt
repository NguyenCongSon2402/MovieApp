package dev.son.movie.data.models.tvshow

import com.google.gson.annotations.SerializedName


data class Params (

  @SerializedName("type_slug"      ) var typeSlug       : String?           = null,
  @SerializedName("filterCategory" ) var filterCategory : ArrayList<String> = arrayListOf(),
  @SerializedName("filterCountry"  ) var filterCountry  : ArrayList<String> = arrayListOf(),
  @SerializedName("filterYear"     ) var filterYear     : String?           = null,
  @SerializedName("filterType"     ) var filterType     : String?           = null,
  @SerializedName("sortField"      ) var sortField      : String?           = null,
  @SerializedName("sortType"       ) var sortType       : String?           = null,
  @SerializedName("pagination"     ) var pagination     : Pagination?       = Pagination()

)