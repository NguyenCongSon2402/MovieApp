package com.oceantech.tracking.ui.home

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.oceantech.tracking.data.models.Home

data class HomeViewState(
    val homes: Async<Home> = Uninitialized,
) : MvRxState {
    fun isLoadding() = homes is Loading
}
