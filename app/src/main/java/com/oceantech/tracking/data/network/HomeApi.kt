package com.oceantech.tracking.data.network

import com.oceantech.tracking.data.models.Home
import retrofit2.http.GET
import io.reactivex.Observable

interface HomeApi {
    @GET("/v1/api/home")
    fun getHome(): Observable<Home>
}