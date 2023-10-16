package com.oceantech.tracking.ui.home

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
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
