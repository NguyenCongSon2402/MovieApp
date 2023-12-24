package dev.son.movie.ui.search

import com.airbnb.mvrx.ActivityViewModelContext
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading

import com.airbnb.mvrx.MvRxViewModelFactory

import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.ViewModelContext
import dev.son.movie.core.TrackingViewModel

import dev.son.movie.network.repository.HomeRepository

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.son.movie.network.models.AddMovieRequestBody
import dev.son.movie.network.repository.SearchRepository


class SearchViewModel @AssistedInject constructor(
    @Assisted state: SearchViewState, private val searchRepo: SearchRepository
) : TrackingViewModel<SearchViewState, SearchViewAction, SearchViewEvent>(state) {
    //    private var job: Job? = null
    override fun handle(action: SearchViewAction) {
        when (action) {
            is SearchViewAction.searchMovie -> handleSearchMovie(action.name)
            is SearchViewAction.addMovie -> handleAddMovie(action.movie)
            is SearchViewAction.getMoviesLatest -> handleGetMoviesLatest()
            is SearchViewAction.deleteMovie -> handleDeleteMovie(action.movieId)
            is SearchViewAction.updateMovie -> handleUpdateMovie(action.movieId,action.movie)
        }
    }

    private fun handleUpdateMovie(movieId: String, movie: AddMovieRequestBody) {
        setState { copy(updateMovie = Loading()) }
        searchRepo.updateMovie(movieId,movie).execute {
            copy(updateMovie = it)
        }
    }

    private fun handleDeleteMovie(movieId: String) {
        setState { copy(deleteMovie = Loading()) }
        searchRepo.deleteMovie(movieId).execute {
            copy(deleteMovie = it)
        }
    }

    private fun handleAddMovie(movie: AddMovieRequestBody) {
        setState { copy(addMovie = Loading()) }
        searchRepo.addMovie(movie).execute {
            copy(addMovie = it)
        }
    }

    private fun handleGetMoviesLatest() {
        setState { copy(getMoviesLatest = Loading()) }
        searchRepo.getMoviesLatest().execute {
            copy(getMoviesLatest = it)
        }
    }

    fun handleRemoveState() =
        setState { copy(search = Uninitialized) }
    fun handleRemoveStateAddMovie() =
        setState { copy(addMovie = Uninitialized) }
    fun handleRemoveStateUpdateMovie() =
        setState { copy(updateMovie = Uninitialized) }
    fun handleRemoveStateMoviesLatest() =
        setState { copy(getMoviesLatest = Uninitialized) }
    fun handleRemoveStateDeleteMovies() =
        setState { copy(deleteMovie = Uninitialized) }

    private fun handleSearchMovie(name: String) {
        setState { copy(search = Loading()) }
        searchRepo.search(name).execute {
            copy(search = it)
        }
    }
    @AssistedFactory
    interface Factory {
        fun create(initialState: SearchViewState): SearchViewModel
    }

    companion object : MvRxViewModelFactory<SearchViewModel, SearchViewState> {
        override fun create(
            viewModelContext: ViewModelContext, state: SearchViewState
        ): SearchViewModel {
            val fatory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as Factory
                is ActivityViewModelContext -> viewModelContext.activity as Factory
            }
            return fatory.create(state)
        }
    }
}