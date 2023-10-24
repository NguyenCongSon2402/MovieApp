package dev.son.movie.network.models.Slug

import com.google.gson.annotations.SerializedName


data class Episodes (

  @SerializedName("server_name" ) var serverName : String?               = null,
  @SerializedName("server_data" ) var serverData : ArrayList<ServerData> = arrayListOf()

)