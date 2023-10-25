package dev.son.movie.network.service

import dev.son.movie.network.models.Slug.Slug
import dev.son.movie.network.models.categories.Categories
import dev.son.movie.network.models.categorymovie.CategoryMovie
import dev.son.movie.network.models.countries.Countries
import dev.son.movie.network.models.home.Home
import dev.son.movie.network.models.home.LongTieng
import dev.son.movie.network.models.home.PhimBo
import dev.son.movie.network.models.home.PhimBoDaHoanThanh
import dev.son.movie.network.models.home.PhimBoDangChieu
import dev.son.movie.network.models.home.PhimHoatHinh
import dev.son.movie.network.models.home.PhimLe
import dev.son.movie.network.models.home.PhimSapChieu
import dev.son.movie.network.models.home.ThuyetMinh
import dev.son.movie.network.models.home.TvShows
import dev.son.movie.network.models.home.VietSub
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

    @GET("/v1/api/danh-sach/tv-shows")
    fun getTVShows(): Observable<TvShows>

    @GET("/v1/api/danh-sach/phim-vietsub")
    fun getVietSub(): Observable<VietSub>

    @GET("/v1/api/danh-sach/phim-thuyet-minh")
    fun getThuyetMinh(): Observable<ThuyetMinh>

    @GET("/v1/api/danh-sach/phim-long-tieng")
    fun getLongTieng(): Observable<LongTieng>

    @GET("/v1/api/danh-sach/phim-bo-dang-chieu")
    fun getPhimBoDangChieu(): Observable<PhimBoDangChieu>

    @GET("/v1/api/danh-sach/phim-bo-hoan-thanh")
    fun getPhimBoDaHoanThanh(): Observable<PhimBoDaHoanThanh>
    @GET("/v1/api/danh-sach/phim-sap-chieu")
    fun getPhimSapChieu(): Observable<PhimSapChieu>
    @GET("/v1/api/quoc-gia")
    fun getCountriesMovies(): Observable<Countries>
    @GET("/v1/api/the-loai")
    fun getCategories(): Observable<Categories>
    @GET("/v1/api/phim/{Slug}")
    fun Slug(@Path("Slug") name: String): Observable<Slug>

    @GET("/v1/api/the-loai/{categories}")
    fun CategoriesMovies(@Path("categories") name: String): Observable<CategoryMovie>
}