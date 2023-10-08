package com.oceantech.tracking.ui.home

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.oceantech.tracking.data.models.Home
import com.oceantech.tracking.data.models.PhimBo
import com.oceantech.tracking.data.models.PhimHoatHinh
import com.oceantech.tracking.data.models.PhimLe

data class HomeViewState(
    val homes: Async<Home> = Uninitialized,
    val phimBo: Async<PhimBo> = Uninitialized,
    val phimLe: Async<PhimLe> = Uninitialized,
    val phimHoatHinh: Async<PhimHoatHinh> = Uninitialized,
) : MvRxState {
    fun isLoadding() = homes is Loading || phimBo is Loading ||phimLe is Loading
}
