package dev.son.movie.ui.home

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import dev.son.movie.data.models.Slug.Slug
import dev.son.movie.data.models.categorymovie.CategoryMovie
import dev.son.movie.data.models.home.Home
import dev.son.movie.data.models.home.LongTieng
import dev.son.movie.data.models.home.PhimBo
import dev.son.movie.data.models.home.PhimBoDaHoanThanh
import dev.son.movie.data.models.home.PhimBoDangChieu
import dev.son.movie.data.models.home.PhimHoatHinh
import dev.son.movie.data.models.home.PhimLe
import dev.son.movie.data.models.home.PhimSapChieu
import dev.son.movie.data.models.home.ThuyetMinh
import dev.son.movie.data.models.home.TvShows
import dev.son.movie.data.models.home.VietSub

data class HomeViewState(
    val homes: Async<Home> = Uninitialized,
    val phimBo: Async<PhimBo> = Uninitialized,
    val phimLe: Async<PhimLe> = Uninitialized,
    val phimHoatHinh: Async<PhimHoatHinh> = Uninitialized,
    val tvShows: Async<TvShows> = Uninitialized,
    val vietsub: Async<VietSub> = Uninitialized,
    val thuyetMinh: Async<ThuyetMinh> = Uninitialized,
    val longTieng: Async<LongTieng> = Uninitialized,
    val phimBoDangChieu: Async<PhimBoDangChieu> = Uninitialized,
    val phimBoHoanThanh: Async<PhimBoDaHoanThanh> = Uninitialized,
    val phimSapChieu: Async<PhimSapChieu> = Uninitialized,
    val slug: Async<Slug> = Uninitialized,
    val categoriesMovies: Async<CategoryMovie> = Uninitialized,
) : MvRxState {
    fun isLoadding() = homes is Loading || phimBo is Loading || phimLe is Loading || slug is Loading
            || categoriesMovies is Loading
}
