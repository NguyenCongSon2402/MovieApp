package com.oceantech.tracking.ui.home

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.oceantech.tracking.data.models.Slug.Slug
import com.oceantech.tracking.data.models.categorymovie.CategoryMovie
import com.oceantech.tracking.data.models.home.Home
import com.oceantech.tracking.data.models.home.PhimBo
import com.oceantech.tracking.data.models.home.PhimHoatHinh
import com.oceantech.tracking.data.models.home.PhimLe

data class HomeViewState(
    val homes: Async<Home> = Uninitialized,
    val phimBo: Async<PhimBo> = Uninitialized,
    val phimLe: Async<PhimLe> = Uninitialized,
    val phimHoatHinh: Async<PhimHoatHinh> = Uninitialized,
    val slug: Async<Slug> = Uninitialized,
    val categoriesMovies: Async<CategoryMovie> = Uninitialized,
) : MvRxState {
    fun isLoadding() = homes is Loading || phimBo is Loading || phimLe is Loading || slug is Loading
            || categoriesMovies is Loading
}
