package dev.son.movie.ui.search

import dev.son.movie.core.NimpeViewModelAction

sealed class SearchViewAction : NimpeViewModelAction {

    data class searchMovie(val name: String) : SearchViewAction()

}
