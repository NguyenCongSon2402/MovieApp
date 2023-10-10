package com.oceantech.tracking.data.models.home

import com.google.gson.annotations.SerializedName


data class Params (

    @SerializedName("type_slug"        ) var typeSlug         : String?           = null,
    @SerializedName("filterCategory"   ) var filterCategory   : ArrayList<String> = arrayListOf(),
    @SerializedName("filterCountry"    ) var filterCountry    : ArrayList<String> = arrayListOf(),
    @SerializedName("filterYear"       ) var filterYear       : String?           = null,
    @SerializedName("sortField"        ) var sortField        : String?           = null,
    @SerializedName("pagination"       ) var pagination       : Pagination?       = Pagination(),
    @SerializedName("itemsUpdateInDay" ) var itemsUpdateInDay : Int?              = null

)