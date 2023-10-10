package com.oceantech.tracking.ui.home

import com.oceantech.tracking.core.NimpeViewModelAction

sealed class HomeViewAction : NimpeViewModelAction {
    object getHome : HomeViewAction()
    object getPhimBo : HomeViewAction()
    object getPhimLe : HomeViewAction()
    object getPhimHoatHinh : HomeViewAction()
    data class getSlug(val name: String) : HomeViewAction()

}
