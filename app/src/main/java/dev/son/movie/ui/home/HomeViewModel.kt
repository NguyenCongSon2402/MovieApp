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
            is HomeViewAction.getGenre -> handleGetGenre()
            is HomeViewAction.getMoviePhimBo -> handleGetMoviesPhimBo(action.genreCode)
            is HomeViewAction.getMoviePhimLe -> handleGetMoviePhimLe(action.genreCode)
            is HomeViewAction.getMoviePhimHoatHinh -> handleGetMoviePhimHoatHinh(action.genreCode)
            is HomeViewAction.getMovieTvShow -> handleGetMovieTvShow(action.genreCode)
            is HomeViewAction.getMovieVietSub -> handleGetMovieVietSub(action.genreCode)
            is HomeViewAction.getMovieThuyetMinh -> handleGetMovieThuyetMinh(action.genreCode)
            is HomeViewAction.getMovieDangChieu -> handleGetMovieDangChieu(action.genreCode)
            is HomeViewAction.getMovieHoanThanh -> handleGetMovieHoanThanh(action.genreCode)
            is HomeViewAction.getMovieSimilar -> handleGetMovieSimilar(action.genreCode)
            is HomeViewAction.getMovieByCategory -> handleGetMovieByCategory(action.genreCode)
            is HomeViewAction.getMovieByCountry -> handleGetMovieByCountries(action.countryCode)
            is HomeViewAction.getMovieRate -> handleGetMovieRate(action.movieId)
            is HomeViewAction.setRateMovie -> handleSetRateMovie(action.movieId, action.rating)
            is HomeViewAction.addFavorite -> handleAddFavorite(action.movieId)
            is HomeViewAction.removeFavorite -> handleRemoveFavorite(action.movieId)
            is HomeViewAction.movieById -> handleMovieById(action.movieId)
            is HomeViewAction.getCommentByMovie -> handleGetCommentByMovie(action.movieId)
            is HomeViewAction.getMoviesRecommendation -> handleGetMoviesRecommendation(action.movieId)
            is HomeViewAction.createComment -> handleCreateComment(action.movieId, action.comment)


            is HomeViewAction.getCountries -> handleCountriesMovies()

            else -> {}
        }
    }

    private fun handleGetMoviesRecommendation(movieId: String) {
        setState { copy(getMoviesRecommendation = Loading()) }
        homeRepo.getMoviesRecommendation(movieId).execute {
            copy(getMoviesRecommendation = it)
        }
    }

    private fun handleGetMovieByCategory(genreCode: String) {
        setState { copy(getMovieByCategory = Loading()) }
        homeRepo.getMovieByCategory(genreCode).execute {
            copy(getMovieByCategory = it)
        }
    }
    private fun handleGetMovieByCountries(countryCode: String) {
        setState { copy(getMovieByCountry = Loading()) }
        homeRepo.getMoviesByCountry(countryCode).execute {
            copy(getMovieByCountry = it)
        }
    }

    private fun handleCreateComment(movieId: String, comment: String) {
        setState { copy(createComment = Loading()) }
        homeRepo.createComment(movieId, comment).execute {
            copy(createComment = it)
        }
    }

    private fun handleGetCommentByMovie(movieId: String) {
        setState { copy(getCommentByMovie = Loading()) }
        homeRepo.getCommentByMovie(movieId).execute {
            copy(getCommentByMovie = it)
        }
    }

    private fun handleMovieById(movieId: String) {
        setState { copy(movieById = Loading()) }
        homeRepo.getMovieById(movieId).execute {
            copy(movieById = it)
        }
    }

    private fun handleRemoveFavorite(movieId: String) {
        setState { copy(removeFavorite = Loading()) }
        homeRepo.removeFavorite(movieId).execute {
            copy(removeFavorite = it)
        }
    }

    private fun handleAddFavorite(movieId: String) {
        setState { copy(addFavorite = Loading()) }
        homeRepo.addFavorite(movieId).execute {
            copy(addFavorite = it)
        }
    }

    private fun handleSetRateMovie(movieId: String, rating: Int) {
        setState { copy(setmovieRate = Loading()) }
        homeRepo.setMovieRate(movieId, rating).execute {
            copy(setmovieRate = it)
        }
    }

    private fun handleGetMovieRate(movieId: String) {
        setState { copy(movieRateRes = Loading()) }
        homeRepo.getMovieRateRes(movieId).execute {
            copy(movieRateRes = it)
        }
    }

    private fun handleGetMovieSimilar(genreCode: String) {
        setState { copy(listMovieSimilar = Loading()) }
        homeRepo.getMovieSimilar(genreCode).execute {
            copy(listMovieSimilar = it)
        }
    }

    private fun handleGetMovieHoanThanh(genreCode: String) {
        setState { copy(movieHoanThanh = Loading()) }
        homeRepo.getMovieHoanThanh(genreCode).execute {
            copy(movieHoanThanh = it)
        }
    }

    private fun handleGetMovieDangChieu(genreCode: String) {
        setState { copy(movieDangChieu = Loading()) }
        homeRepo.getMovieDangChieu(genreCode).execute {
            copy(movieDangChieu = it)
        }
    }

    private fun handleGetMovieThuyetMinh(genreCode: String) {
        setState { copy(movieThuyetMinh = Loading()) }
        homeRepo.getMovieThuyetMinh(genreCode).execute {
            copy(movieThuyetMinh = it)
        }
    }

    private fun handleGetMovieVietSub(genreCode: String) {
        setState { copy(movieVietSub = Loading()) }
        homeRepo.getMovieVietSub(genreCode).execute {
            copy(movieVietSub = it)
        }
    }

    private fun handleGetMovieTvShow(genreCode: String) {
        setState { copy(movieTvShow = Loading()) }
        homeRepo.getMovieTvShow(genreCode).execute {
            copy(movieTvShow = it)
        }
    }

    private fun handleGetMoviePhimHoatHinh(genreCode: String) {
        setState { copy(phimHoatHinh = Loading()) }
        homeRepo.getMovieHoatHinh(genreCode).execute {
            copy(phimHoatHinh = it)
        }
    }

    private fun handleGetMoviePhimLe(genreCode: String) {
        setState { copy(moviePhimLe = Loading()) }
        homeRepo.getMoviePhimLe(genreCode).execute {
            copy(moviePhimLe = it)
        }
    }

    private fun handleGetMoviesPhimBo(genreCode: String) {
        setState { copy(moviePhimBo = Loading()) }
        homeRepo.getMoviePhimBo(genreCode).execute {
            copy(moviePhimBo = it)
        }
    }

    private fun handleGetGenre() {
        setState { copy(genre = Loading()) }
        homeRepo.getGenre().execute {
            copy(genre = it)
        }
    }


    fun handleRemoveStateGetGenre() =
        setState { copy(genre = Uninitialized) }

    fun handleRemoveStateCommentByMovie() =
        setState { copy(getCommentByMovie = Uninitialized) }

    fun handleRemoveStateCreateComment() =
        setState { copy(createComment = Uninitialized) }

    fun handleRemoveGetMovieById() =
        setState { copy(movieById = Uninitialized) }

    fun handleRemoveStateRemoveFavorite() =
        setState { copy(removeFavorite = Uninitialized) }

    fun handleRemoveStatePhimBo() =
        setState { copy(moviePhimBo = Uninitialized) }

    fun handleRemoveStatePhimle() =
        setState { copy(moviePhimLe = Uninitialized) }

    fun handleRemoveStatePhimHoatHinh() =
        setState { copy(phimHoatHinh = Uninitialized) }

    fun handleRemoveAddFavorite() =
        setState { copy(addFavorite = Uninitialized) }

    fun handleRemoveStateCategoriesMovies() =
        setState { copy(listMovieSimilar = Uninitialized) }
    fun handleRemoveStateMoviesRecommendation() =
        setState { copy(getMoviesRecommendation = Uninitialized) }

    fun handleRemoveStateSetmovieRate() =
        setState { copy(setmovieRate = Uninitialized) }

    fun handleRemoveStateMovieRateRes() =
        setState { copy(movieRateRes = Uninitialized) }

    fun handleRemoveStateTvShows() =
        setState { copy(movieTvShow = Uninitialized) }

    fun handleRemoveStateVietsub() =
        setState { copy(movieVietSub = Uninitialized) }

    fun handleRemoveStateThuyetMinh() =
        setState { copy(movieThuyetMinh = Uninitialized) }

    fun handleRemoveStatePhimBoDangChieu() =
        setState { copy(movieDangChieu = Uninitialized) }

    fun handleRemoveStatePhimBoHoanThanh() =
        setState { copy(movieHoanThanh = Uninitialized) }

    fun handleRemoveStateCountries() =
        setState { copy(countriesMovies = Uninitialized) }


    private fun handleCountriesMovies() {
        setState { copy(countriesMovies = Loading()) }
        homeRepo.getCountries().execute {
            copy(countriesMovies = it)
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