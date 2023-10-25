package dev.son.movie.ui.home

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
            is HomeViewAction.getTvShows -> handleGetTvSHows()
            is HomeViewAction.getVietSub -> handleGetPhimVietSub()
            is HomeViewAction.getThuyetMinh -> handleGetPhimThuyetMinh()
            is HomeViewAction.getPhimLongTieng -> handleGetPhimLongTieng()
            is HomeViewAction.getPhimBoDangChieu -> handleGetPhimBoDangChieu()
            is HomeViewAction.getPhimBoDaHoanThanh -> handleGetPhimBoDaHoanThanh()
            is HomeViewAction.getSlug -> handleGetSlug(action.name)
            is HomeViewAction.getCategoriesMovies -> handleGetCategories(action.name)
            is HomeViewAction.getPhimSapChieu -> handleGetComingSoon()
            else -> {}
        }
    }


    fun handleRemoveStateHome() =
        setState { copy(homes = Uninitialized) }

    fun handleRemoveStatePhimBo() =
        setState { copy(phimBo = Uninitialized) }

    fun handleRemoveStatePhimle() =
        setState { copy(phimLe = Uninitialized) }

    fun handleRemoveStatePhimHoatHinh() =
        setState { copy(phimHoatHinh = Uninitialized) }

    fun handleRemoveStateSlug() =
        setState { copy(slug = Uninitialized) }

    fun handleRemoveStateCategoriesMovies() =
        setState { copy(categoriesMovies = Uninitialized) }
    fun handleRemoveStateTvShows() =
        setState { copy(tvShows = Uninitialized) }

    fun handleRemoveStateVietsub() =
        setState { copy(vietsub = Uninitialized) }

    fun handleRemoveStateThuyetMinh() =
        setState { copy(thuyetMinh = Uninitialized) }

    fun handleRemoveStateLongTieng() =
        setState { copy(longTieng = Uninitialized) }

    fun handleRemoveStatePhimBoDangChieu() =
        setState { copy(phimBoDangChieu = Uninitialized) }

    fun handleRemoveStatePhimBoHoanThanh() =
        setState { copy(phimBoHoanThanh = Uninitialized) }
    fun handleRemoveState() =
        setState { copy(phimBoHoanThanh = Uninitialized) }



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

    private fun handleGetCategories(name: String) {
        setState { copy(categoriesMovies = Loading()) }
        homeRepo.categoriesMovies(name).execute {
            copy(categoriesMovies = it)
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

    private fun handleGetTvSHows() {
        setState { copy(tvShows = Loading()) }
        homeRepo.getTvShows().execute {
            copy(tvShows = it)
        }
    }

    private fun handleGetPhimVietSub() {
        setState { copy(vietsub = Loading()) }
        homeRepo.getVietSub().execute {
            copy(vietsub = it)
        }
    }

    private fun handleGetPhimThuyetMinh() {
        setState { copy(thuyetMinh = Loading()) }
        homeRepo.getThuyetMinh().execute {
            copy(thuyetMinh = it)
        }
    }

    private fun handleGetPhimLongTieng() {
        setState { copy(longTieng = Loading()) }
        homeRepo.getPhimLongTieng().execute {
            copy(longTieng = it)
        }
    }

    private fun handleGetPhimBoDangChieu() {
        setState { copy(phimBoDangChieu = Loading()) }
        homeRepo.getPhimBoDangChieu().execute {
            copy(phimBoDangChieu = it)
        }
    }

    private fun handleGetPhimBoDaHoanThanh() {
        setState { copy(phimBoHoanThanh = Loading()) }
        homeRepo.getPhimBoDaHoanThanh().execute {
            copy(phimBoHoanThanh = it)
        }
    }
    private fun handleGetComingSoon() {
        setState { copy(phimSapChieu = Loading()) }
        homeRepo.getPhimSapChieu().execute {
            copy(phimSapChieu = it)
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