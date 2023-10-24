package dev.son.movie.network.repository


import dev.son.movie.network.models.Slug.Slug
import dev.son.movie.network.models.categorymovie.CategoryMovie
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
import dev.son.movie.network.service.HomeApi
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