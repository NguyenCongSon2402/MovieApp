package com.oceantech.tracking.data.repository


import com.oceantech.tracking.data.models.Slug.Slug
import com.oceantech.tracking.data.models.categorymovie.CategoryMovie
import com.oceantech.tracking.data.models.home.Home
import com.oceantech.tracking.data.models.home.LongTieng
import com.oceantech.tracking.data.models.home.PhimBo
import com.oceantech.tracking.data.models.home.PhimBoDaHoanThanh
import com.oceantech.tracking.data.models.home.PhimBoDangChieu
import com.oceantech.tracking.data.models.home.PhimHoatHinh
import com.oceantech.tracking.data.models.home.PhimLe
import com.oceantech.tracking.data.models.home.PhimSapChieu
import com.oceantech.tracking.data.models.home.ThuyetMinh
import com.oceantech.tracking.data.models.home.TvShows
import com.oceantech.tracking.data.models.home.VietSub
import com.oceantech.tracking.data.network.HomeApi
import javax.inject.Singleton
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers


@Singleton
class HomeRepository(val api: HomeApi) {
    fun getHome(): Observable<Home> = api.getHome().subscribeOn(Schedulers.io())

    fun getPhimBo(): Observable<PhimBo> = api.getPhimBo().subscribeOn(Schedulers.io())
    fun getPhimLe(): Observable<PhimLe> = api.getPhimLe().subscribeOn(Schedulers.io())
    fun getPhimHoatHinh(): Observable<PhimHoatHinh> =
        api.getPhimHoatHinh().subscribeOn(Schedulers.io())

    fun getTvShows(): Observable<TvShows> =
        api.getTVShows().subscribeOn(Schedulers.io())

    fun getVietSub(): Observable<VietSub> =
        api.getVietSub().subscribeOn(Schedulers.io())

    fun getThuyetMinh(): Observable<ThuyetMinh> =
        api.getThuyetMinh().subscribeOn(Schedulers.io())

    fun getPhimLongTieng(): Observable<LongTieng> =
        api.getLongTieng().subscribeOn(Schedulers.io())

    fun getPhimBoDangChieu(): Observable<PhimBoDangChieu> =
        api.getPhimBoDangChieu().subscribeOn(Schedulers.io())

    fun getPhimBoDaHoanThanh(): Observable<PhimBoDaHoanThanh> =
        api.getPhimBoDaHoanThanh().subscribeOn(Schedulers.io())
    fun getPhimSapChieu(): Observable<PhimSapChieu> =
        api.getPhimSapChieu().subscribeOn(Schedulers.io())

    fun slug(name: String): Observable<Slug> = api.Slug(name).subscribeOn(Schedulers.io())
    fun categoriesMovies(name: String): Observable<CategoryMovie> =
        api.CategoriesMovies(name).subscribeOn(Schedulers.io())
}