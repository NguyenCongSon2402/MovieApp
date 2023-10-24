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
import dev.son.movie.network.repository.SearchRepository


class SearchViewModel @AssistedInject constructor(
    @Assisted state: SearchViewState, private val searchRepo: SearchRepository
) : TrackingViewModel<SearchViewState, SearchViewAction, SearchViewEvent>(state) {
    //    private var job: Job? = null
    override fun handle(action: SearchViewAction) {
        when (action) {
            is SearchViewAction.searchMovie -> handleSearchMovie(action.name)
        }
    }

    fun handleRemoveState() =
        setState { copy(search = Uninitialized) }

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