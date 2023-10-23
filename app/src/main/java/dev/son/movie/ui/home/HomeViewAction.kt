package dev.son.movie.ui.home

import dev.son.movie.core.NimpeViewModelAction

sealed class HomeViewAction : NimpeViewModelAction {
    object getHome : HomeViewAction()
    object getPhimBo : HomeViewAction()
    object getPhimLe : HomeViewAction()
    object getPhimHoatHinh : HomeViewAction()
    object getTvShows : HomeViewAction()
    object getVietSub : HomeViewAction()
    object getThuyetMinh : HomeViewAction()
    object getPhimLongTieng : HomeViewAction()
    object getPhimBoDangChieu : HomeViewAction()
    object getPhimBoDaHoanThanh : HomeViewAction()
    object getPhimSapChieu : HomeViewAction()
    data class getSlug(val name: String) : HomeViewAction()
    data class getCategoriesMovies(val name: String) : HomeViewAction()

}
