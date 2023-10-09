package com.oceantech.tracking.ui.home

import com.airbnb.mvrx.ActivityViewModelContext
import com.airbnb.mvrx.FragmentViewModelContext

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
    private var job: Job? = null
    override fun handle(action: HomeViewAction) {
        when (action) {
            is HomeViewAction.getHome -> handleGetHome()
            is HomeViewAction.getPhimBo -> handleGetPhimBo()
            is HomeViewAction.getPhimLe -> handleGetPhimLe()
            is HomeViewAction.getPhimHoatHinh -> handleGetPhimHoatHinh()
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


    private fun handleGetHome() {
        setState { copy(homes = Uninitialized) }
        homeRepo.getHome().execute {
            copy(homes = it)
        }

    }

    private fun handleGetPhimBo() {
        setState { copy(phimBo = Uninitialized) }
        homeRepo.getPhimBo().execute {
            copy(phimBo = it)
        }
    }
    private fun handleGetPhimLe() {
        setState { copy(phimLe = Uninitialized) }
        homeRepo.getPhimLe().execute {
            copy(phimLe = it)
        }

    }
    private fun handleGetPhimHoatHinh() {
        setState { copy(phimHoatHinh = Uninitialized) }
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