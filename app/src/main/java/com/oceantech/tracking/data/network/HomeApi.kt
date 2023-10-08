package com.oceantech.tracking.data.network

import com.oceantech.tracking.data.models.Home
import com.oceantech.tracking.data.models.PhimBo
import com.oceantech.tracking.data.models.PhimHoatHinh
import com.oceantech.tracking.data.models.PhimLe
import retrofit2.http.GET
import io.reactivex.Observable

interface HomeApi {
    @GET("/v1/api/home")
    fun getHome(): Observable<Home>

    @GET("/v1/api/danh-sach/phim-bo")
    fun getPhimBo(): Observable<PhimBo>
    @GET("/v1/api/danh-sach/phim-le")
    fun getPhimLe(): Observable<PhimLe>
    @GET("/v1/api/danh-sach/hoat-hinh")
    fun getPhimHoatHinh(): Observable<PhimHoatHinh>
}