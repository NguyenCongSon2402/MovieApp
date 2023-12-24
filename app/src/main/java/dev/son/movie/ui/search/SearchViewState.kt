package dev.son.movie.ui.search

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import dev.son.movie.network.models.movie.ApiResponse
import dev.son.movie.network.models.movie.Movie
import dev.son.movie.network.models.search.Search

data class SearchViewState(
    val search: Async<ApiResponse<List<Movie>>> = Uninitialized,
    val getMoviesLatest: Async<ApiResponse<List<Movie>>> = Uninitialized,
    val addMovie: Async<ApiResponse<String>> = Uninitialized,
    val deleteMovie: Async<ApiResponse<String>> = Uninitialized,
    val updateMovie: Async<ApiResponse<Movie>> = Uninitialized,

) : MvRxState {
    fun isLoadding() = search is Loading
}
