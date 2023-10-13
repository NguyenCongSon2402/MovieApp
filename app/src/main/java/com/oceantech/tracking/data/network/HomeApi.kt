package com.oceantech.tracking.data.network

import com.oceantech.tracking.data.models.Slug.Slug
import com.oceantech.tracking.data.models.categorymovie.CategoryMovie
import com.oceantech.tracking.data.models.home.Home
import com.oceantech.tracking.data.models.home.PhimBo
import com.oceantech.tracking.data.models.home.PhimHoatHinh
import com.oceantech.tracking.data.models.home.PhimLe
import retrofit2.http.GET
import io.reactivex.Observable
import retrofit2.http.Path

interface HomeApi {
    @GET("/v1/api/home")
    fun getHome(): Observable<Home>

    @GET("/v1/api/danh-sach/phim-bo")
    fun getPhimBo(): Observable<PhimBo>
    @GET("/v1/api/danh-sach/phim-le")
    fun getPhimLe(): Observable<PhimLe>
    @GET("/v1/api/danh-sach/hoat-hinh")
    fun getPhimHoatHinh(): Observable<PhimHoatHinh>

    @GET("/v1/api/phim/{Slug}")
    fun Slug(@Path("Slug") name:String): Observable<Slug>
    @GET("/v1/api/the-loai/{categories}")
    fun CategoriesMovies(@Path("categories") name:String): Observable<CategoryMovie>
}