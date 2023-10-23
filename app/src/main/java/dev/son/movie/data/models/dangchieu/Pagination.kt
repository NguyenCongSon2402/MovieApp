package dev.son.movie.data.models.dangchieu

import com.google.gson.annotations.SerializedName


data class Pagination (

  @SerializedName("totalItems"        ) var totalItems        : Int? = null,
  @SerializedName("totalItemsPerPage" ) var totalItemsPerPage : Int? = null,
  @SerializedName("currentPage"       ) var currentPage       : Int? = null,
  @SerializedName("pageRanges"        ) var pageRanges        : Int? = null

)