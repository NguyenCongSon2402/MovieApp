package com.oceantech.tracking.ui.home

import com.airbnb.mvrx.ActivityViewModelContext
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading

import com.airbnb.mvrx.MvRxViewModelFactory

import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.ViewModelContext
import com.oceantech.tracking.core.TrackingViewModel

import com.oceantech.tracking.data.repository.HomeRepository

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job


class HomeViewModel @AssistedInject constructor(
    @Assisted state: HomeViewState, private val homeRepo: HomeRepository
) : TrackingViewModel<HomeViewState, HomeViewAction, HomeViewEvent>(state) {
//    private var job: Job? = null
    override fun handle(action: HomeViewAction) {
        when (action) {
            is HomeViewAction.getHome -> handleGetHome()
            is HomeViewAction.getPhimBo -> handleGetPhimBo()
            is HomeViewAction.getPhimLe -> handleGetPhimLe()
            is HomeViewAction.getPhimHoatHinh -> handleGetPhimHoatHinh()
            is HomeViewAction.getSlug -> handleGetSlug(action.name)
        }
    }



    fun handleRemoveStateHome() =
        setState { copy(homes = Uninitialized) }

    fun handleRemoveStatePhimBo() =
        setState { copy(phimBo= Uninitialized) }
    fun handleRemoveStatePhimle() =
        setState { copy(phimLe =Uninitialized) }
    fun handleRemoveStatePhimHoatHinh() =
        setState { copy(phimHoatHinh = Uninitialized) }
    fun handleRemoveStateSlug() =
        setState { copy(slug = Uninitialized) }


    private fun handleGetHome() {
        setState { copy(homes = Loading()) }
        homeRepo.getHome().execute {
            copy(homes = it)
        }

    }
    private fun handleGetSlug(name: String) {
        setState { copy(slug = Loading()) }
        homeRepo.slug(name).execute {
            copy(slug = it)
        }
    }

    private fun handleGetPhimBo() {
        setState { copy(phimBo = Loading()) }
        homeRepo.getPhimBo().execute {
            copy(phimBo = it)
        }
    }
    private fun handleGetPhimLe() {
        setState { copy(phimLe = Loading()) }
        homeRepo.getPhimLe().execute {
            copy(phimLe = it)
        }

    }
    private fun handleGetPhimHoatHinh() {
        setState { copy(phimHoatHinh = Loading()) }
        homeRepo.getPhimHoatHinh().execute {
            copy(phimHoatHinh = it)
        }
    }


    @AssistedFactory
    interface Factory {
        fun create(initialState: HomeViewState): HomeViewModel
    }

    companion object : MvRxViewModelFactory<HomeViewModel, HomeViewState> {
        override fun create(
            viewModelContext: ViewModelContext, state: HomeViewState
        ): HomeViewModel {
            val fatory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as Factory
                is ActivityViewModelContext -> viewModelContext.activity as Factory
            }
            return fatory.create(state)
        }
    }
}