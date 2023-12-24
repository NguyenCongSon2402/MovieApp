package dev.son.movie.ui.search

import dev.son.movie.core.NimpeViewModelAction
import dev.son.movie.network.models.AddMovieRequestBody

sealed class SearchViewAction : NimpeViewModelAction {

    data class searchMovie(val name: String) : SearchViewAction()
    data class deleteMovie(val movieId: String) : SearchViewAction()
    data class addMovie(val movie:AddMovieRequestBody) : SearchViewAction()
    data class updateMovie(val movieId: String,val movie:AddMovieRequestBody) : SearchViewAction()
    object getMoviesLatest : SearchViewAction()

}
