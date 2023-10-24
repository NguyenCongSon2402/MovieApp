package dev.son.movie.ui.search

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
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
import dev.son.movie.network.models.search.Search

data class SearchViewState(
    val search: Async<Search> = Uninitialized,

) : MvRxState {
    fun isLoadding() = search is Loading
}
