package dev.son.movie.network.models.countries

import com.google.gson.annotations.SerializedName


data class Data(

    @SerializedName("items") var items: ArrayList<Items> = arrayListOf()

)